package com.critter.critter_backend.domain;

import java.util.Set;

public enum CritterType {
    OCTOPUS(EcosystemTheme.OCEAN, Set.of(FoodType.SHRIMP, FoodType.FISH)),
    PENGUIN(EcosystemTheme.OCEAN, Set.of(FoodType.FISH, FoodType.SHRIMP, FoodType.MEAT)),
    TURTLE(EcosystemTheme.OCEAN, Set.of(FoodType.SHRIMP, FoodType.WEED, FoodType.BUG, FoodType.FRUIT, FoodType.MEAT)),
    
    SQUIRREL(EcosystemTheme.FOREST, Set.of(FoodType.EGG, FoodType.BUG, FoodType.FRUIT)),
    REDPANDA(EcosystemTheme.FOREST, Set.of(FoodType.WEED, FoodType.EGG, FoodType.BAMBOO, FoodType.FRUIT)),
    FOX(EcosystemTheme.FOREST, Set.of(FoodType.SHRIMP, FoodType.FISH, FoodType.MEAT, FoodType.BUG, FoodType.KIBBLE, FoodType.EGG, FoodType.FRUIT)),
    
    RABBIT(EcosystemTheme.GRASSLAND, Set.of(FoodType.WEED, FoodType.FRUIT)),
    CAT(EcosystemTheme.GRASSLAND, Set.of(FoodType.SHRIMP, FoodType.FISH, FoodType.MEAT, FoodType.EGG, FoodType.KIBBLE)),
    DOG(EcosystemTheme.GRASSLAND, Set.of(FoodType.SHRIMP, FoodType.FISH, FoodType.MEAT, FoodType.EGG, FoodType.FRUIT, FoodType.KIBBLE));

    private final EcosystemTheme requiredTheme;
    private final Set<FoodType> preferredFoods;

    CritterType(EcosystemTheme requiredTheme, Set<FoodType> preferredFoods) {
        this.requiredTheme = requiredTheme;
        this.preferredFoods = preferredFoods;
    }

    public EcosystemTheme getRequiredTheme() {
        return this.requiredTheme;
    }

    public boolean likes(FoodType foodType) {
        return preferredFoods.contains(foodType);
    }
}