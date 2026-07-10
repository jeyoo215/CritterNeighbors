package com.critter.critter_backend.exception.habitat;

// 서식지 데이터 없음
public class HabitatNotFoundException extends RuntimeException {
    public HabitatNotFoundException(String message) {
        super(message);
    }
}
