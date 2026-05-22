package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.GoalDTO;
import com.solutec.competition_service.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/matches/{matchId}")
    public ResponseEntity<GoalDTO> recordGoal(@PathVariable Long matchId, @RequestBody GoalDTO dto) {
        return ResponseEntity.ok(goalService.recordGoal(matchId, dto));
    }

    @GetMapping("/matches/{matchId}")
    public ResponseEntity<List<GoalDTO>> getGoalsByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(goalService.getGoalsByMatch(matchId));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
}