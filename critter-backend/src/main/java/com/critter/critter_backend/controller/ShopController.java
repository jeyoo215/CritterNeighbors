package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.domain.FoodType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.service.FoodService;
import com.critter.critter_backend.service.ShopCritterService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ecosystems/{roomId}/shop")
@CrossOrigin(origins = "http://localhost:5173")
public class ShopController {

    private final ShopCritterService shopCritterService;
    private final CritterService critterService;
    private final EcosystemMemoryStorage memoryStorage;
    private final AccountRepository accountRepository;
    private final FoodService foodService;

    // 불러오기
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getShopItems(@PathVariable(required = false) Long roomId) {
        Map<String, Object> items = new HashMap<>();
        items.put("critters", shopCritterService.getAllShopCritter());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/adopt")
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
    
        // 웹소켓 메모리에 투입 (이때 savedCritter의 type이 그대로 들어가므로 프론트에서 자동 인식)
        CritterLocationDto newCritterDto = new CritterLocationDto(
            savedCritter.getCritterId(),
            savedCritter.getCritterName(),
            savedCritter.getCritterType().name(),
            Math.random() * 800,
            Math.random() * 600,
            "IDLE",
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


    @PostMapping("/buy-food")
    public ResponseEntity<?> buyFood(@RequestBody Map<String, Object> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        FoodType foodType = FoodType.valueOf(request.get("foodType").toString());
        Long roomId = Long.valueOf(request.get("roomId").toString());

        // 서비스 호출
        try {
            foodService.purchaseFood(userId, roomId, foodType);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}