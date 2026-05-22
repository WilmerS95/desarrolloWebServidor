package com.solutec.competition_service.dto;

import lombok.Data;

import java.util.Set;

@Data
public class GroupDTO {
    private Long groupID;
    private Long tournamentID;
    private String name;
    private String description;
    // IDs de equipos que pertenecen al grupo (útil para frontend)
    private Set<Long> teamIDs;
}