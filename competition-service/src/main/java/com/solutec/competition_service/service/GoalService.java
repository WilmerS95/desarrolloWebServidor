package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.GoalDTO;
import com.solutec.competition_service.entity.*;
import com.solutec.competition_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    public GoalDTO recordGoal(Long matchId, GoalDTO dto) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        Player scorer = playerRepository.findById(dto.getScorerId())
                .orElseThrow(() -> new RuntimeException("Goleador no encontrado"));

        Goal goal = new Goal();
        goal.setMatch(match);
        goal.setScorer(scorer);
        goal.setMinute(dto.getMinute());
        goal.setExtraMinute(dto.getExtraMinute());

        if (dto.getAssistId() != null) {
            Player assist = playerRepository.findById(dto.getAssistId())
                    .orElseThrow(() -> new RuntimeException("Asistidor no encontrado"));
            goal.setAssist(assist);
        }

        Goal saved = goalRepository.save(goal);
        return mapToDTO(saved);
    }

    public List<GoalDTO> getGoalsByMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        return goalRepository.findByMatch(match)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new RuntimeException("Gol no encontrado");
        }
        goalRepository.deleteById(goalId);
    }

    private GoalDTO mapToDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setGoalId(goal.getGoalID());
        dto.setMatchId(goal.getMatch().getMatchID());
        dto.setScorerId(goal.getScorer().getPlayerID());
        dto.setScorerName(goal.getScorer().getFirstName() + " " + goal.getScorer().getLastNameFirst());
        if (goal.getAssist() != null) {
            dto.setAssistId(goal.getAssist().getPlayerID());
            dto.setAssistName(goal.getAssist().getFirstName() + " " + goal.getAssist().getLastNameFirst());
        }
        dto.setMinute(goal.getMinute());
        dto.setExtraMinute(goal.getExtraMinute());
        dto.setCreatedAt(goal.getCreatedAt());
        return dto;
    }
}