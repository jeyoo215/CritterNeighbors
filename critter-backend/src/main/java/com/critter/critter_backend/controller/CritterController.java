package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.service.CritterTemplateService;
import com.critter.critter_backend.service.PointService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ecosystems/{roomId}/critters")
@CrossOrigin(origins = "http://localhost:5173")
public class CritterController {

    private final CritterTemplateService critterTemplateService;
    private final CritterService critterService;
    private final PointService pointService;
    private final EcosystemMemoryStorage memoryStorage;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> adoptCritter(
        @PathVariable("roomId") Long roomId,
        @RequestBody Map<String, Object> requestBody) {

        if (!requestBody.containsKey("userId")) return ResponseEntity.badRequest().body("userId 누락");
        Long userId = Long.valueOf(requestBody.get("userId").toString());
        CritterType critterType = CritterType.valueOf(requestBody.get("critterType").toString());
        Long price = critterTemplateService.getTemplatePrice(critterType);
        String critterName = requestBody.get("critterName").toString();
        
        log.info("입양 시도 유저 ID: {}, 현재 포인트: {}", userId, accountRepository.findById(userId).get().getPoint());

        // 포인트 차감 시도
        try {
            pointService.spend(userId, price);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // DB에 해당 타입으로 저장
        Critter savedCritter = critterService.adoptCritter(roomId, critterName, critterType);
    
        // 웹소켓 메모리에 투입 (이때 savedCritter의 type이 그대로 들어가니 프론트에서 자동 인식)
        CritterLocationDto newCritterDto = new CritterLocationDto(
            savedCritter.getCritterId(),
            savedCritter.getCritterName(),
            savedCritter.getCritterType().name(),
            Math.random() * 800, // Double
            Math.random() * 600, // Double
            "IDLE",              // String
            Double.valueOf(Math.random() * 2 - 1),
            Double.valueOf(Math.random() * 2 - 1)
        );

        memoryStorage.addCritter(roomId, newCritterDto);
    
        Account updatedUser = accountRepository.findById(userId).orElseThrow();
    
        // 입양한 크리처 정보와 최신 유저 정보를 같이 넘겨주기 위해 Map으로
        Map<String, Object> response = new HashMap<>();
        response.put("critter", savedCritter);
        response.put("user", updatedUser); // 이게 있어야 프론트가 포인트를 갱신해!

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/shop-items")
    public ResponseEntity<List<Map<String, Object>>> getShopItems() {
        return ResponseEntity.ok(critterTemplateService.getAllCritterTemplates());
    }
}