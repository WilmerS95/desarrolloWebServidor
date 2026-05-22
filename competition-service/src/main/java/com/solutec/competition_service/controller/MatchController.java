package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.MatchDTO;
import com.solutec.competition_service.entity.enums.MatchStatus;
import com.solutec.competition_service.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(@RequestBody MatchDTO dto) {
        return ResponseEntity.ok(matchService.createMatch(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/tournaments/{tournamentId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.getMatchesByTournament(tournamentId));
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(matchService.getMatchesByGroup(groupId));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<MatchDTO> updateScore(@PathVariable Long id, @RequestParam Integer homeGoals, @RequestParam Integer awayGoals) {
        return ResponseEntity.ok(matchService.updateMatchScore(id, homeGoals, awayGoals));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchDTO> updateMatch(@PathVariable Long id, @RequestBody MatchDTO dto) {
        return ResponseEntity.ok(matchService.updateMatch(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MatchDTO> updateStatus(@PathVariable Long id, @RequestParam MatchStatus status) {
        return ResponseEntity.ok(matchService.updateMatchStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}