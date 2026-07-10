package com.critter.critter_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.critter.critter_backend.exception.account.BadCredentialsException;
import com.critter.critter_backend.exception.account.DuplicateUsernameException;
import com.critter.critter_backend.exception.account.ForbiddenException;
import com.critter.critter_backend.exception.account.SelfGuestbookForbiddenException;
import com.critter.critter_backend.exception.habitat.HabitatNotFoundException;
import com.critter.critter_backend.exception.habitat.InvalidHabitatException;
import com.critter.critter_backend.exception.point.NoPointException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 전역에서 발생하는 예외를 JSON 응답으로 가공하는 어노테이션
public class GlobalExceptionHandler {

    // account

    // 로그인 비밀번호 불일치 예외
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "UNAUTHORIZED"); // 401 권한 없음
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 회원가입 아이디 중복
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUsername(DuplicateUsernameException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "CONFLICT"); // 409 충돌 (이미 존재함)
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 접근 권한 없음
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "FORBIDDEN"); // 403
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 본인 방 자가 방명록 차단
    @ExceptionHandler(SelfGuestbookForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleSelfGuestbookForbidden(SelfGuestbookForbiddenException e) {
        Map<String, String> response = new HashMap<>();
        
        response.put("error", "BAD_REQUEST");
        response.put("message", e.getMessage()); // "본인의 생태계 방에는 방명록을 남길 수 없음"
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 에러
    }


    // habitat

    // 환경 불일치로 인한 분양 요청 거부
    @ExceptionHandler(InvalidHabitatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidHabitat(InvalidHabitatException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BAD_REQUEST");
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 에러
    }

    // 서식지 존재 X
    @ExceptionHandler(HabitatNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleHabitatNotFound(HabitatNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "NOT_FOUND");
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404
    }


    // point

    // 포인트 부족
    @ExceptionHandler(NoPointException.class)
    public ResponseEntity<Map<String, String>> handleNoPoint(NoPointException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BAD_REQUEST"); // 400
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    
    // 접근할 타겟 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "NOT_FOUND"); // 404
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 기본 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "NOT_FOUND");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404
    }
}