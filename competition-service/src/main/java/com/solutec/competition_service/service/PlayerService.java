package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.PlayerDTO;
import com.solutec.competition_service.entity.Player;
import com.solutec.competition_service.entity.Team;
import com.solutec.competition_service.repository.PlayerRepository;
import com.solutec.competition_service.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerDTO registerPlayer(Long teamId, PlayerDTO dto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Verificar si ya existe un jugador con ese DPI
        if (dto.getDpi() != null && !dto.getDpi().isEmpty()) {
            playerRepository.findByDpi(dto.getDpi())
                    .forEach(p -> {
                        throw new RuntimeException("Ya existe un jugador con ese DPI");
                    });
        }

        Player player = new Player();
        player.setTeam(team);
        player.setUserID(dto.getUserID());
        player.setFirstName(dto.getFirstName());
        player.setLastNameFirst(dto.getLastNameFirst());
        player.setLastNameSecond(dto.getLastNameSecond());
        player.setShirtNumber(dto.getShirtNumber());
        player.setPosition(dto.getPosition());
        player.setBirthDate(dto.getBirthDate());
        player.setNationality(dto.getNationality());
        player.setDpi(dto.getDpi());
        player.setPhotoUrl(dto.getPhotoUrl());
        player.setIsActive(true);

        Player saved = playerRepository.save(player);
        return mapToDTO(saved);
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        return mapToDTO(player);
    }

    public List<PlayerDTO> getPlayersByTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        return playerRepository.findByTeam(team)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO dto) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        player.setFirstName(dto.getFirstName());
        player.setLastNameFirst(dto.getLastNameFirst());
        player.setLastNameSecond(dto.getLastNameSecond());
        player.setShirtNumber(dto.getShirtNumber());
        player.setPosition(dto.getPosition());
        player.setBirthDate(dto.getBirthDate());
        player.setNationality(dto.getNationality());
        player.setPhotoUrl(dto.getPhotoUrl());
        player.setIsActive(dto.getIsActive());

        Player updated = playerRepository.save(player);
        return mapToDTO(updated);
    }

    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Jugador no encontrado");
        }
        playerRepository.deleteById(id);
    }

    public void deactivatePlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        player.setIsActive(false);
        playerRepository.save(player);
    }

    private PlayerDTO mapToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setPlayerID(player.getPlayerID());
        dto.setTeamID(player.getTeam().getTeamID());
        dto.setUserID(player.getUserID());
        dto.setFirstName(player.getFirstName());
        dto.setLastNameFirst(player.getLastNameFirst());
        dto.setLastNameSecond(player.getLastNameSecond());
        dto.setShirtNumber(player.getShirtNumber());
        dto.setPosition(player.getPosition());
        dto.setBirthDate(player.getBirthDate());
        dto.setNationality(player.getNationality());
        dto.setDpi(player.getDpi());
        dto.setPhotoUrl(player.getPhotoUrl());
        dto.setRegistrationDate(player.getRegistrationDate());
        dto.setIsActive(player.getIsActive());
        return dto;
    }
}