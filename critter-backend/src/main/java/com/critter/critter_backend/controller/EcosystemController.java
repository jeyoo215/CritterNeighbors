package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.LogTargetType;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.service.EcosystemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    /*
        방 생성
        POST
    */
    @PostMapping
    public ResponseEntity<Ecosystem> createRoom(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        Ecosystem newRoom = ecosystemService.createRoom(
                userId,
                request.get("roomName"),
                request.get("roomTheme")
        );

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, null, newRoom.getRoomId(), LogTargetType.ROOM, ActionType.CREATE_ROOM));

        return ResponseEntity.ok(newRoom);
    }

    /*
        내 방 목록 조회
        GET
    */
    @GetMapping("/my")
    public ResponseEntity<List<Ecosystem>> getMyRooms(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        List<Ecosystem> myRooms = ecosystemService.getRoomsByUserId(userId);
        return ResponseEntity.ok(myRooms);
    }

    /*
        남의 방
        GET
    */
    @GetMapping("/random")
    public ResponseEntity<List<Ecosystem>> getRandomRooms(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        List<Ecosystem> randomRooms = ecosystemService.getRandomRoomsExcludingUser(userId);
        return ResponseEntity.ok(randomRooms);
    }
}