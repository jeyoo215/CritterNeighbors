package com.critter.critter_backend.controller;

import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.service.EcosystemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ecosystems")
@RequiredArgsConstructor
public class EcosystemController {

    private final EcosystemService ecosystemService;

    /**
     * 🚪 방 만들기 API
     */
    @PostMapping
    public ResponseEntity<Ecosystem> createRoom(@RequestBody Map<String, String> requestBody, HttpSession session) {
        // 로그인 세션에서 USER_ID 추출
        // 테스트용 임시 제거
        // Long userId = (Long) session.getAttribute("USER_ID");
        // if (userId == null) {
        //     return ResponseEntity.status(401).build(); // 로그인 안 됨 에러
        // }

        // 임시 테스트용 가상 유저 ID 1번 주입 (또는 아무 숫자나)
        Long userId = 1L;
        // 테스트

        Ecosystem newRoom = ecosystemService.createRoom(
                userId,
                requestBody.get("roomName"),
                requestBody.get("roomTheme")
        );

        return ResponseEntity.ok(newRoom);
    }

    /**
     * 🔍 내 방 목록 조회 API
     */
    @GetMapping("/my")
    public ResponseEntity<List<Ecosystem>> getMyRooms(HttpSession session) {
        // 테스트용 주석
        // Long userId = (Long) session.getAttribute("USER_ID");
        // if (userId == null) {
        //     return ResponseEntity.status(401).build();
        // }
        Long userId = 1L;
        // 테스트용

        List<Ecosystem> myRooms = ecosystemService.getRoomsByUserId(userId);
        return ResponseEntity.ok(myRooms);
    }
}