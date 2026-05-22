package com.solutec.competition_service.dto;

import com.solutec.competition_service.entity.enums.MatchStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDTO {
    private Long matchID;
    private Long tournamentID;
    private Long groupID;
    private Long homeTeamID;
    private Long awayTeamID;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDateTime matchDate;
    private String stadium;
    private Long refereeUserID;
    private Integer homeGoals;
    private Integer awayGoals;
    private MatchStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}