package com.solutec.competition_service.entity.enums;

public enum PhaseType {
    GROUP("Fase de Grupos"),
    ROUND_16("Dieciseisavos"),
    QUARTERFINALS("Cuartos de Final"),
    SEMIFINALS("Semifinales"),
    FINALS("Final");

    private String displayName;

    PhaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}