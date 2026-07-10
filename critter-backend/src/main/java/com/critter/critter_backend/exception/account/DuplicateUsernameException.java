package com.critter.critter_backend.exception.account;

// 회원가입 아이디 중복 예외
public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}