package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.PlayerDTO;
import com.solutec.competition_service.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/teams/{teamId}")
    public ResponseEntity<PlayerDTO> registerPlayer(@PathVariable Long teamId, @RequestBody PlayerDTO dto) {
        return ResponseEntity.ok(playerService.registerPlayer(teamId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO dto) {
        return ResponseEntity.ok(playerService.updatePlayer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePlayer(@PathVariable Long id) {
        playerService.deactivatePlayer(id);
        return ResponseEntity.noContent().build();
    }
}