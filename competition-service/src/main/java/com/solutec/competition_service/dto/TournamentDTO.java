package com.solutec.competition_service.dto;

import com.solutec.competition_service.entity.enums.TournamentStatus;
import com.solutec.competition_service.entity.enums.TournamentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentDTO {
    private Long tournamentID;
    private String name;
    private String description;
    private TournamentType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxTeams;
    private Integer teamsPerGroup;
    private Integer advanceTeams;
    private TournamentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}