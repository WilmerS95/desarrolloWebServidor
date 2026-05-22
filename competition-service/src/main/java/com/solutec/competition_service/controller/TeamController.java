package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.TeamDTO;
import com.solutec.competition_service.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/tournaments/{tournamentId}")
    public ResponseEntity<TeamDTO> registerTeam(@PathVariable Long tournamentId, @RequestBody TeamDTO dto) {
        return ResponseEntity.ok(teamService.registerTeam(tournamentId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/tournaments/{tournamentId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(teamService.getTeamsByTournament(tournamentId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeams(@RequestParam String q) {
        return ResponseEntity.ok(teamService.searchTeamsByName(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody TeamDTO dto) {
        return ResponseEntity.ok(teamService.updateTeam(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}