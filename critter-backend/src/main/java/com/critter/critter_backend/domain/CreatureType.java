package com.critter.critter_backend.domain;

public enum CreatureType {
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

    CreatureType(EcosystemTheme requiredTheme) {
        this.requiredTheme = requiredTheme;
    }

    public EcosystemTheme getRequiredTheme() {
        return this.requiredTheme;
    }
}