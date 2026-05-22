package com.solutec.competition_service.entity.enums;

public enum CardType {
    YELLOW("Amarilla"),
    RED("Roja");

    private String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}