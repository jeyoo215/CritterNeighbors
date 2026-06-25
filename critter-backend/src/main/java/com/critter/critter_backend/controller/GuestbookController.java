package com.critter.critter_backend.controller;

import com.critter.critter_backend.entity.Guestbook;
import com.critter.critter_backend.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ecosystems/{roomId}/guestbooks") // RESTful하게 방 번호 하위 주소로 설계!
@CrossOrigin(origins = "http://localhost:5173") // 리액트 연동 대비 CORS 해제는 필수!
public class GuestbookController {

    private final GuestbookService guestbookService;

    /*
     * 1. 방명록 비동기 등록 API
     * POST /api/ecosystems/{roomId}/guestbooks
    */
    @PostMapping
    public ResponseEntity<Guestbook> createGuestbook(
            @PathVariable("roomId") Long roomId,
            @RequestBody Map<String, Object> requestBody) {
        
        // JSON 바디에서 작성자 ID와 내용 파싱 (프론트가 { "writerId": 1, "content": "방가방가" } 보낸다고 가정)
        Long writerId = Long.valueOf(requestBody.get("writerId").toString());
        String content = requestBody.get("content").toString();

        // 서비스 호출 (만약 자가 방명록이면 우리가 만든 글로벌 핸들러가 자동으로 400 에러 튕겨줌!)
        Guestbook savedGuestbook = guestbookService.createGuestbook(roomId, writerId, content);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedGuestbook);
    }

    /*
     * 2. 특정 방의 방명록 목록 비동기 조회 API
     * GET /api/ecosystems/{roomId}/guestbooks
    */
    @GetMapping
    public ResponseEntity<List<Guestbook>> getGuestbookList(@PathVariable("roomId") Long roomId) {
        List<Guestbook> guestbooks = guestbookService.getGuestbooksByRoom(roomId);
        return ResponseEntity.ok(guestbooks);
    }
}