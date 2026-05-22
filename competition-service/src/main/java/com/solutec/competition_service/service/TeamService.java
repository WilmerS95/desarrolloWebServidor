package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.TeamDTO;
import com.solutec.competition_service.entity.Team;
import com.solutec.competition_service.entity.Tournament;
import com.solutec.competition_service.repository.TeamRepository;
import com.solutec.competition_service.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    public TeamDTO registerTeam(Long tournamentId, TeamDTO dto) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        Team team = new Team();
        team.setTournament(tournament);
        team.setName(dto.getName());
        team.setAcronym(dto.getAcronym());
        team.setCity(dto.getCity());
        team.setFoundedDate(dto.getFoundedDate());
        team.setPhotoUrl(dto.getPhotoUrl());
        team.setShirtColor(dto.getShirtColor());

        Team saved = teamRepository.save(team);
        return mapToDTO(saved);
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        return mapToDTO(team);
    }

    public List<TeamDTO> getTeamsByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        return teamRepository.findByTournament(tournament)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamDTO> searchTeamsByName(String name) {
        return teamRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO updateTeam(Long id, TeamDTO dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        team.setName(dto.getName());
        team.setAcronym(dto.getAcronym());
        team.setCity(dto.getCity());
        team.setFoundedDate(dto.getFoundedDate());
        team.setPhotoUrl(dto.getPhotoUrl());
        team.setShirtColor(dto.getShirtColor());

        Team updated = teamRepository.save(team);
        return mapToDTO(updated);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Equipo no encontrado");
        }
        teamRepository.deleteById(id);
    }

    private TeamDTO mapToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setTeamID(team.getTeamID());
        dto.setTournamentID(team.getTournament().getTournamentID());
        dto.setName(team.getName());
        dto.setAcronym(team.getAcronym());
        dto.setCity(team.getCity());
        dto.setFoundedDate(team.getFoundedDate());
        dto.setPhotoUrl(team.getPhotoUrl());
        dto.setShirtColor(team.getShirtColor());
        dto.setRegistrationDate(team.getRegistrationDate());
        return dto;
    }
}