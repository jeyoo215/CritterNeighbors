package com.critter.critter_backend.exception;

// 본인 방에 자가 방명록 작성을 시도할 때 차단하는 예외
public class SelfGuestbookForbiddenException extends RuntimeException {
    public SelfGuestbookForbiddenException(String message) {
        super(message);
    }
}