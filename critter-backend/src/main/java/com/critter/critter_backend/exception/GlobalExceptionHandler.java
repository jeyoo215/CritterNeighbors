package com.critter.critter_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 💡 팩트: 전역에서 발생하는 예외를 JSON 응답으로 가공하는 어노테이션!
public class GlobalExceptionHandler {

    // 본인 방 자가 방명록 차단 예외
    @ExceptionHandler(SelfGuestbookForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleSelfGuestbookForbidden(SelfGuestbookForbiddenException e) {
        Map<String, String> response = new HashMap<>();
        
        response.put("error", "BAD_REQUEST");
        response.put("message", e.getMessage()); // "본인의 생태계 방에는 방명록을 남길 수 없습니다."
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 프론트한테 400 에러 던짐
    }

    // 환경 상성 불일치로 인한 분양 요청 거부 예외
    // 400 에러 반환
    @ExceptionHandler(InvalidHabitatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidHabitat(InvalidHabitatException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BAD_REQUEST");
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 기본 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "NOT_FOUND");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}