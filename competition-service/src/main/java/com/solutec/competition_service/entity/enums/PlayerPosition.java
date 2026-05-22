package com.solutec.competition_service.entity.enums;

public enum PlayerPosition {
    ARQUERO("Arquero"),
    DEFENSA("Defensa"),
    CENTROCAMPISTA("Centrocampista"),
    DELANTERO("Delantero");

    private String displayName;

    PlayerPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}