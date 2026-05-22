package com.solutec.competition_service.entity.enums;

public enum TournamentType {
    FUTBOL_5("Fútbol 5"),
    FUTBOL_7("Fútbol 7"),
    FUTBOL_11("Fútbol 11"),
    PAPI_FUTBOL("Papi Fútbol");

    private String displayName;

    TournamentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}