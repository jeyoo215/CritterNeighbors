package com.critter.critter_backend.exception;

// 방의 테마 환경과 동물의 필수 서식지가 일치하지 않을 때 발생하는 예외
public class InvalidHabitatException extends RuntimeException {
    public InvalidHabitatException(String message) {
        super(message);
    }
}