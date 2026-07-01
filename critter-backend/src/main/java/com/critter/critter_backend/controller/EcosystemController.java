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
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class EcosystemController {

    private final EcosystemService ecosystemService;

    /*
        방 생성
        POST
    */
    @PostMapping
    public ResponseEntity<Ecosystem> createRoom(@RequestBody Map<String, String> request, HttpSession session) {
        // 로그인 세션에서 USER_ID 추출
        // 테스트용 임시 제거
        // Long userId = (Long) session.getAttribute("USER_ID");
        // if (userId == null) {
        //     return ResponseEntity.status(401).build(); // 로그인 안 됨 에러
        // }

        // 테스트용으로 프론트에서 받아오기
        Long userId = Long.valueOf(request.get("userId").toString());
        // 테스트

        Ecosystem newRoom = ecosystemService.createRoom(
                userId,
                request.get("roomName"),
                request.get("roomTheme")
        );

        return ResponseEntity.ok(newRoom);
    }

    /*
        내 방 목록 조회
        GET
    */
    @GetMapping("/my")
    public ResponseEntity<List<Ecosystem>> getMyRooms(@RequestParam Long userId, HttpSession session) {

        List<Ecosystem> myRooms = ecosystemService.getRoomsByUserId(userId);
        return ResponseEntity.ok(myRooms);
    }

    /*
        남의 방
        GET
    */
    @GetMapping("/random")
    public ResponseEntity<List<Ecosystem>> getRandomRooms(@RequestParam("userId") Long userId) {
        
        List<Ecosystem> randomRooms = ecosystemService.getRandomRoomsExcludingUser(userId);
        return ResponseEntity.ok(randomRooms);
    }
}