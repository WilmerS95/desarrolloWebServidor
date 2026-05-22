package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.TournamentDTO;
import com.solutec.competition_service.entity.Tournament;
import com.solutec.competition_service.entity.enums.TournamentStatus;
import com.solutec.competition_service.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentDTO createTournament(TournamentDTO dto) {
        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setType(dto.getType());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setLocation(dto.getLocation());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setTeamsPerGroup(dto.getTeamsPerGroup());
        tournament.setAdvanceTeams(dto.getAdvanceTeams());
        tournament.setStatus(TournamentStatus.PLANNING);

        Tournament saved = tournamentRepository.save(tournament);
        return mapToDTO(saved);
    }

    public TournamentDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        return mapToDTO(tournament);
    }

    public List<TournamentDTO> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> getTournamentsByStatus(TournamentStatus status) {
        return tournamentRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> searchTournamentsByName(String name) {
        return tournamentRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TournamentDTO updateTournament(Long id, TournamentDTO dto) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setType(dto.getType());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setLocation(dto.getLocation());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setTeamsPerGroup(dto.getTeamsPerGroup());
        tournament.setAdvanceTeams(dto.getAdvanceTeams());

        Tournament updated = tournamentRepository.save(tournament);
        return mapToDTO(updated);
    }

    public TournamentDTO updateTournamentStatus(Long id, TournamentStatus status) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        tournament.setStatus(status);
        Tournament updated = tournamentRepository.save(tournament);
        return mapToDTO(updated);
    }

    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new RuntimeException("Torneo no encontrado");
        }
        tournamentRepository.deleteById(id);
    }

    private TournamentDTO mapToDTO(Tournament tournament) {
        TournamentDTO dto = new TournamentDTO();
        dto.setTournamentID(tournament.getTournamentID());
        dto.setName(tournament.getName());
        dto.setDescription(tournament.getDescription());
        dto.setType(tournament.getType());
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setLocation(tournament.getLocation());
        dto.setMaxTeams(tournament.getMaxTeams());
        dto.setTeamsPerGroup(tournament.getTeamsPerGroup());
        dto.setAdvanceTeams(tournament.getAdvanceTeams());
        dto.setStatus(tournament.getStatus());
        dto.setCreatedAt(tournament.getCreatedAt());
        dto.setUpdatedAt(tournament.getUpdatedAt());
        return dto;
    }
}