package com.solutec.competition_service.dto;

import lombok.Data;

@Data
public class StatisticsDTO {
    private Long playerID;
    private String playerName;
    private Long teamID;
    private String teamName;
    private Long tournamentID;
    private Integer goals;
    private Integer assists;
    private Integer yellowCards;
    private Integer redCards;
    private Integer matches;
    private Double averageGoalsPerMatch;

    public StatisticsDTO() {}

    public StatisticsDTO(Long playerID, String playerName, Long teamID, String teamName,
                        Long tournamentID, Integer goals, Integer assists,
                        Integer yellowCards, Integer redCards, Integer matches) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.teamID = teamID;
        this.teamName = teamName;
        this.tournamentID = tournamentID;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.matches = matches;
        this.averageGoalsPerMatch = matches > 0 ? (double) goals / matches : 0;
    }
}