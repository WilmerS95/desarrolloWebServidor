package com.solutec.competition_service.dto;

import lombok.Data;

@Data
public class StandingDTO {
    private Long standingID;
    private Long groupID;
    private Long teamID;
    private String teamName;
    private Integer position;
    private Integer matches;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer goalsFor;
    private Integer goalsAgainst;
    private Integer goalDifference;
    private Integer points;
}