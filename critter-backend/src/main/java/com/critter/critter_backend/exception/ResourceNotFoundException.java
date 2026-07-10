package com.critter.critter_backend.exception;

// 자원(타겟) 존재 X 예외
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}