package com.solutec.competition_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TeamDTO {
    private Long teamID;
    private Long tournamentID;
    private String name;
    private String acronym;
    private String city;
    private LocalDate foundedDate;
    private String photoUrl;
    private String shirtColor;
    private LocalDateTime registrationDate;
}