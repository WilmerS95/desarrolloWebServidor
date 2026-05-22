package com.solutec.competition_service.entity.enums;

public enum TournamentStatus {
    PLANNING("Planificación"),
    IN_PROGRESS("En Progreso"),
    FINISHED("Finalizado"),
    CANCELLED("Cancelado");

    private String displayName;

    TournamentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}