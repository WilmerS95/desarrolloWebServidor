package com.solutec.competition_service.dto;

import com.solutec.competition_service.entity.enums.PlayerPosition;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlayerDTO {
    private Long playerID;
    private Long teamID;
    private Long userID;
    private String firstName;
    private String lastNameFirst;
    private String lastNameSecond;
    private Integer shirtNumber;
    private PlayerPosition position;
    private LocalDate birthDate;
    private String nationality;
    private String dpi;
    private String photoUrl;
    private LocalDateTime registrationDate;
    private Boolean isActive;
}