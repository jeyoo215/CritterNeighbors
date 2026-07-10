package com.critter.critter_backend.exception.account;

// 접근 권한 없음 예외
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
