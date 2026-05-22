package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.StatisticsDTO;
import com.solutec.competition_service.dto.StandingDTO;
import com.solutec.competition_service.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/tournaments/{tournamentId}/top-scorers")
    public ResponseEntity<List<StatisticsDTO>> topScorers(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTopScorers(tournamentId));
    }

    @GetMapping("/groups/{groupId}/standings")
    public ResponseEntity<List<StandingDTO>> standingsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(statisticsService.getStandingsByGroup(groupId));
    }

    @PostMapping("/groups/{groupId}/standings/recalculate")
    public ResponseEntity<Void> recalculateStandings(@PathVariable Long groupId) {
        statisticsService.recalculateStandings(groupId);
        return ResponseEntity.ok().build();
    }
}