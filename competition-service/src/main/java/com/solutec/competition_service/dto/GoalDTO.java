package com.solutec.competition_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoalDTO {
    private Long goalId;
    private Long matchId;
    private Long scorerId;
    private String scorerName;
    private Long assistId;
    private String assistName;
    private Integer minute;
    private Integer extraMinute;
    private LocalDateTime createdAt;

    public GoalDTO() {}
}