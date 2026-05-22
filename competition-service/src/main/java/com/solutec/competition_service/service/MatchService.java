package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.MatchDTO;
import com.solutec.competition_service.entity.*;
import com.solutec.competition_service.entity.enums.MatchStatus;
import com.solutec.competition_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final GroupRepository groupRepository;
    private final TeamRepository teamRepository;

    public MatchDTO createMatch(MatchDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.getTournamentID())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        Team homeTeam = teamRepository.findById(dto.getHomeTeamID())
                .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));

        Team awayTeam = teamRepository.findById(dto.getAwayTeamID())
                .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        Match match = new Match();
        match.setTournament(tournament);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setMatchDate(dto.getMatchDate());
        match.setStadium(dto.getStadium());
        match.setRefereeUserID(dto.getRefereeUserID());
        match.setStatus(MatchStatus.SCHEDULED);

        if (dto.getGroupID() != null) {
            Group group = groupRepository.findById(dto.getGroupID())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
            match.setGroup(group);
        }

        Match saved = matchRepository.save(match);
        return mapToDTO(saved);
    }

    public MatchDTO getMatchById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        return mapToDTO(match);
    }

    public List<MatchDTO> getMatchesByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        return matchRepository.findByTournament(tournament)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return matchRepository.findByGroup(group)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MatchDTO updateMatchScore(Long id, Integer homeGoals, Integer awayGoals) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);

        Match updated = matchRepository.save(match);
        return mapToDTO(updated);
    }

    public MatchDTO updateMatchStatus(Long id, MatchStatus status) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        match.setStatus(status);
        Match updated = matchRepository.save(match);
        return mapToDTO(updated);
    }

    public MatchDTO updateMatch(Long id, MatchDTO dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        match.setMatchDate(dto.getMatchDate());
        match.setStadium(dto.getStadium());
        match.setRefereeUserID(dto.getRefereeUserID());
        match.setStatus(dto.getStatus());

        Match updated = matchRepository.save(match);
        return mapToDTO(updated);
    }

    public void deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new RuntimeException("Partido no encontrado");
        }
        matchRepository.deleteById(id);
    }

    private MatchDTO mapToDTO(Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setMatchID(match.getMatchID());
        dto.setTournamentID(match.getTournament().getTournamentID());
        if (match.getGroup() != null) {
            dto.setGroupID(match.getGroup().getGroupID());
        }
        dto.setHomeTeamID(match.getHomeTeam().getTeamID());
        dto.setAwayTeamID(match.getAwayTeam().getTeamID());
        dto.setHomeTeamName(match.getHomeTeam().getName());
        dto.setAwayTeamName(match.getAwayTeam().getName());
        dto.setMatchDate(match.getMatchDate());
        dto.setStadium(match.getStadium());
        dto.setRefereeUserID(match.getRefereeUserID());
        dto.setHomeGoals(match.getHomeGoals());
        dto.setAwayGoals(match.getAwayGoals());
        dto.setStatus(match.getStatus());
        dto.setCreatedAt(match.getCreatedAt());
        dto.setUpdatedAt(match.getUpdatedAt());
        return dto;
    }
}