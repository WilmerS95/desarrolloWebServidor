package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.StatisticsDTO;
import com.solutec.competition_service.dto.StandingDTO;
import com.solutec.competition_service.entity.*;
import com.solutec.competition_service.entity.enums.CardType;
import com.solutec.competition_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsService {

    private final GoalRepository goalRepository;
    private final CardRepository cardRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final StandingRepository standingRepository;
    private final GroupRepository groupRepository;

    /**
     * Obtiene los goleadores de un torneo
     */
    public List<StatisticsDTO> getTopScorers(Long tournamentId) {
        Map<Long, StatisticsDTO> statsMap = new HashMap<>();

        // Obtener todos los goles del torneo
        List<Goal> goals = matchRepository.findAll().stream()
                .filter(m -> m.getTournament().getTournamentID().equals(tournamentId))
                .flatMap(m -> goalRepository.findByMatch(m).stream())
                .collect(Collectors.toList());

        // Procesar goles
        for (Goal goal : goals) {
            Player player = goal.getScorer();
            Long playerId = player.getPlayerID();

            statsMap.computeIfAbsent(playerId, k -> new StatisticsDTO(
                    player.getPlayerID(),
                    player.getFirstName() + " " + player.getLastNameFirst(),
                    player.getTeam().getTeamID(),
                    player.getTeam().getName(),
                    tournamentId,
                    0, 0, 0, 0, 0
            )).setGoals(statsMap.get(playerId).getGoals() + 1);

            // Contar asistencias
            if (goal.getAssist() != null) {
                Player assist = goal.getAssist();
                Long assistId = assist.getPlayerID();
                statsMap.computeIfAbsent(assistId, k -> new StatisticsDTO(
                        assist.getPlayerID(),
                        assist.getFirstName() + " " + assist.getLastNameFirst(),
                        assist.getTeam().getTeamID(),
                        assist.getTeam().getName(),
                        tournamentId,
                        0, 0, 0, 0, 0
                )).setAssists(statsMap.get(assistId).getAssists() + 1);
            }
        }

        // Procesar tarjetas
        List<Card> cards = cardRepository.findAll().stream()
                .filter(c -> c.getMatch().getTournament().getTournamentID().equals(tournamentId))
                .collect(Collectors.toList());

        for (Card card : cards) {
            Player player = card.getPlayer();
            Long playerId = player.getPlayerID();

            StatisticsDTO stat = statsMap.computeIfAbsent(playerId, k -> new StatisticsDTO(
                    player.getPlayerID(),
                    player.getFirstName() + " " + player.getLastNameFirst(),
                    player.getTeam().getTeamID(),
                    player.getTeam().getName(),
                    tournamentId,
                    0, 0, 0, 0, 0
            ));

            if (card.getType() == CardType.YELLOW) {
                stat.setYellowCards(stat.getYellowCards() + 1);
            } else if (card.getType() == CardType.RED) {
                stat.setRedCards(stat.getRedCards() + 1);
            }
        }

        // Ordenar por goles (DESC)
        return statsMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getGoals(), a.getGoals()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la tabla de posiciones de un grupo
     */
    public List<StandingDTO> getStandingsByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Standing> standings = standingRepository.findByGroup(group);
        return standings.stream()
                .sorted(Comparator.comparing(Standing::getPosition))
                .map(this::mapStandingToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recalcula la tabla de posiciones de un grupo
     */
    public void recalculateStandings(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Inicializar o limpiar standings
        List<Standing> standings = standingRepository.findByGroup(group);
        standingRepository.deleteAll(standings);

        // Crear entrada para cada equipo del grupo
        group.getTeams().forEach(team -> {
            Standing standing = new Standing();
            standing.setGroup(group);
            standing.setTeam(team);
            standing.setMatches(0);
            standing.setWins(0);
            standing.setDraws(0);
            standing.setLosses(0);
            standing.setGoalsFor(0);
            standing.setGoalsAgainst(0);
            standing.setPoints(0);
            standingRepository.save(standing);
        });

        // Procesar todos los partidos del grupo
        List<Match> matches = matchRepository.findByGroup(group);
        for (Match match : matches) {
            updateStandingsForMatch(group, match);
        }

        // Asignar posiciones
        List<Standing> updatedStandings = standingRepository.findByGroup(group).stream()
                .sorted((a, b) -> {
                    int pointsComparison = Integer.compare(b.getPoints(), a.getPoints());
                    if (pointsComparison != 0) return pointsComparison;
                    return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
                })
                .collect(Collectors.toList());

        for (int i = 0; i < updatedStandings.size(); i++) {
            updatedStandings.get(i).setPosition(i + 1);
            standingRepository.save(updatedStandings.get(i));
        }
    }

    private void updateStandingsForMatch(Group group, Match match) {
        Standing homeStanding = standingRepository.findByGroup(group).stream()
                .filter(s -> s.getTeam().getTeamID().equals(match.getHomeTeam().getTeamID()))
                .findFirst()
                .orElse(null);

        Standing awayStanding = standingRepository.findByGroup(group).stream()
                .filter(s -> s.getTeam().getTeamID().equals(match.getAwayTeam().getTeamID()))
                .findFirst()
                .orElse(null);

        if (homeStanding != null && awayStanding != null) {
            homeStanding.setMatches(homeStanding.getMatches() + 1);
            awayStanding.setMatches(awayStanding.getMatches() + 1);

            homeStanding.setGoalsFor(homeStanding.getGoalsFor() + match.getHomeGoals());
            homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + match.getAwayGoals());

            awayStanding.setGoalsFor(awayStanding.getGoalsFor() + match.getAwayGoals());
            awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + match.getHomeGoals());

            if (match.getHomeGoals() > match.getAwayGoals()) {
                homeStanding.setWins(homeStanding.getWins() + 1);
                awayStanding.setLosses(awayStanding.getLosses() + 1);
                homeStanding.setPoints(homeStanding.getPoints() + 3);
            } else if (match.getHomeGoals() < match.getAwayGoals()) {
                awayStanding.setWins(awayStanding.getWins() + 1);
                homeStanding.setLosses(homeStanding.getLosses() + 1);
                awayStanding.setPoints(awayStanding.getPoints() + 3);
            } else {
                homeStanding.setDraws(homeStanding.getDraws() + 1);
                awayStanding.setDraws(awayStanding.getDraws() + 1);
                homeStanding.setPoints(homeStanding.getPoints() + 1);
                awayStanding.setPoints(awayStanding.getPoints() + 1);
            }

            homeStanding.updatePoints();
            awayStanding.updatePoints();

            standingRepository.save(homeStanding);
            standingRepository.save(awayStanding);
        }
    }

    private StandingDTO mapStandingToDTO(Standing standing) {
        StandingDTO dto = new StandingDTO();
        dto.setStandingID(standing.getStandingID());
        dto.setGroupID(standing.getGroup().getGroupID());
        dto.setTeamID(standing.getTeam().getTeamID());
        dto.setTeamName(standing.getTeam().getName());
        dto.setPosition(standing.getPosition());
        dto.setMatches(standing.getMatches());
        dto.setWins(standing.getWins());
        dto.setDraws(standing.getDraws());
        dto.setLosses(standing.getLosses());
        dto.setGoalsFor(standing.getGoalsFor());
        dto.setGoalsAgainst(standing.getGoalsAgainst());
        dto.setGoalDifference(standing.getGoalDifference());
        dto.setPoints(standing.getPoints());
        return dto;
    }
}