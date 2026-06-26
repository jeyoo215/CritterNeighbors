package com.critter.critter_backend.domain;

public enum CritterType {
    OCTOPUS(EcosystemTheme.OCEAN),
    TURTLE(EcosystemTheme.OCEAN),
    PENGUIN(EcosystemTheme.OCEAN),
    
    REDPANDA(EcosystemTheme.FOREST),
    FOX(EcosystemTheme.FOREST),
    SQUIRREL(EcosystemTheme.FOREST),
    
    RABBIT(EcosystemTheme.GRASSLAND),
    DOG(EcosystemTheme.GRASSLAND),
    CAT(EcosystemTheme.GRASSLAND);

    private final EcosystemTheme requiredTheme;

    CritterType(EcosystemTheme requiredTheme) {
        this.requiredTheme = requiredTheme;
    }

    public EcosystemTheme getRequiredTheme() {
        return this.requiredTheme;
    }
}