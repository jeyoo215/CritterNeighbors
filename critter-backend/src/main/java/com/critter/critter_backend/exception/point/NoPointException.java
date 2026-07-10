package com.critter.critter_backend.exception.point;

// 포인트 부족
public class NoPointException extends RuntimeException {
    public NoPointException(String message) {
        super(message);
    }
}
