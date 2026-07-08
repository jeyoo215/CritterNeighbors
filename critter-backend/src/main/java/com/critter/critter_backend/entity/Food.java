package com.critter.critter_backend.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.critter.critter_backend.domain.FoodType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
    private String id;
    private FoodType type;
    private double x, y;
    
    @Builder.Default
    private int eatingCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public boolean isFinished() {
        return Duration.between(this.createdAt, LocalDateTime.now()).toMinutes() >= 10;
    }

    @JsonProperty("isSinking")
    private boolean isSinking;
    
    public void startEating() {
        this.eatingCount++;
    }
}