package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.service.ShopCritterService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import jakarta.servlet.http.HttpSession;
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

    private final ShopCritterService shopCritterService;
    private final CritterService critterService;
    private final EcosystemMemoryStorage memoryStorage;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> adoptCritter(
        @PathVariable("roomId") Long roomId,
        @RequestBody Map<String, Object> requestBody,
        HttpSession session) {
            
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        CritterType critterType = CritterType.valueOf(requestBody.get("critterType").toString());
        Long price = shopCritterService.getCritterPrice(critterType);
        String critterName = requestBody.get("critterName").toString();

        // 포인트 차감 시도
        Critter savedCritter;
        try {
            savedCritter = critterService.adoptCritter(userId, roomId, price, critterName, critterType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    
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
    
        Map<String, Object> response = new HashMap<>();
        response.put("critter", savedCritter);
        response.put("user", updatedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/shop-items")
    public ResponseEntity<List<Map<String, Object>>> getShopItems() {
        return ResponseEntity.ok(shopCritterService.getAllShopCritter());
    }
}