package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.TournamentDTO;
import com.solutec.competition_service.entity.enums.TournamentStatus;
import com.solutec.competition_service.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/competitions/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping("/public/test")
    public ResponseEntity<?> testPublic() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Módulo de Competiciones funcional",
                "service", "competition-service",
                "port", 8085
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<?> testPrivate() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Endpoint privado - JWT validado",
                "status", "autenticado"
        ));
    }

    @PostMapping
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentDTO dto) {
        return ResponseEntity.ok(tournamentService.createTournament(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournament(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id));
    }

    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TournamentDTO>> searchByName(@RequestParam String q) {
        return ResponseEntity.ok(tournamentService.searchTournamentsByName(q));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TournamentDTO>> getByStatus(@PathVariable TournamentStatus status) {
        return ResponseEntity.ok(tournamentService.getTournamentsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(@PathVariable Long id, @RequestBody TournamentDTO dto) {
        return ResponseEntity.ok(tournamentService.updateTournament(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TournamentDTO> updateStatus(@PathVariable Long id, @RequestParam TournamentStatus status) {
        return ResponseEntity.ok(tournamentService.updateTournamentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}