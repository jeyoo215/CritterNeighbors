package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ecosystems/{roomId}/critters")
@CrossOrigin(origins = "http://localhost:5173")
public class CritterController {

    private final CritterService critterService;
    private final EcosystemMemoryStorage memoryStorage; // 👈 추가! 메모리 접근 권한 장착

    @PostMapping
    public ResponseEntity<Critter> adoptCritter(
            @PathVariable("roomId") Long roomId,
            @RequestBody Map<String, Object> requestBody) {

        String critterName = requestBody.get("critterName").toString();
        CritterType critterType = CritterType.valueOf(requestBody.get("critterType").toString());

        // 1. DB에 저장
        Critter savedCritter = critterService.adoptCritter(roomId, critterName, critterType);
        
        // 2. 🚨 실시간 메모리에 즉시 반영 (이게 없어서 펭귄이 안 나왔던 거야!)
        // DB 엔티티를 웹소켓용 DTO로 변환해서 메모리에 넣어줌
        CritterLocationDto newCritterDto = new CritterLocationDto(
            savedCritter.getCritterId(),
            savedCritter.getCritterName(),
            savedCritter.getCritterType().name(),
            Math.random() * 800, // 초기 X 좌표 (필요하면 DB에서 가져오게 수정 가능)
            Math.random() * 600, // 초기 Y 좌표
            "IDLE",
            Math.random() * 2 - 1,
            Math.random() * 2 - 1
        );
        
        memoryStorage.addCritter(roomId, newCritterDto);
        log.info("🐧 펭귄 {} 생성 완료, 메모리 방에 투입!", critterName);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCritter);
    }
}