package com.critter.critter_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CritterLocationDto {
    private Long critterId;
    private String name;
    private String creatureType;
    private Double x;
    private Double y;
    private String status;
    private Double vx; // 속도
    private Double vy; // 방향
}