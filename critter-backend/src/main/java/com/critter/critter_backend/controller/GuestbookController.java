package com.critter.critter_backend.controller;

import com.critter.critter_backend.dto.GuestbookRequestDto;
import com.critter.critter_backend.entity.Guestbook;
import com.critter.critter_backend.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    /*
        방명록 조회
        GET /api/guestbooks/1
    */
    @GetMapping("/api/ecosystems/{roomId}/guestbook")
    public ResponseEntity<List<Guestbook>> getGuestbookList(@PathVariable Long roomId) {
        return ResponseEntity.ok(guestbookService.getGuestbooksByRoom(roomId));
    }

    /*
        방명록 작성
        POST /api/guestbooks/1
    */
    @PostMapping("/api/ecosystems/{roomId}/guestbook")
    public ResponseEntity<Guestbook> createGuestbook(
            @PathVariable Long roomId,
            @RequestBody GuestbookRequestDto request) {
        
        Guestbook saved = guestbookService.createGuestbook(roomId, request.getWriterId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}