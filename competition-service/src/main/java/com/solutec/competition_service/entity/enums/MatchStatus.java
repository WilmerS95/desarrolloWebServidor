package com.solutec.competition_service.entity.enums;

public enum MatchStatus {
    SCHEDULED("Programado"),
    IN_PROGRESS("En Progreso"),
    FINISHED("Finalizado"),
    POSTPONED("Aplazado"),
    CANCELLED("Cancelado");

    private String displayName;

    MatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}