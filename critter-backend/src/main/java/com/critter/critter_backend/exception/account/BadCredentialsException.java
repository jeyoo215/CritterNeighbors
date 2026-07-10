package com.critter.critter_backend.exception.account;

// 로그인 아이디 비밀번호 불일치 예외
public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}