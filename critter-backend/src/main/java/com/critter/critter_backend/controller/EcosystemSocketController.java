package com.critter.critter_backend.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.FoodType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.repository.EcosystemRepository;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.service.FoodService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EcosystemSocketController {

    private final EcosystemMemoryStorage ecosystemMemoryStorage;
    private final CritterService critterService;
    private final EcosystemRepository ecosystemRepository;
    private final FoodService foodService;

    private final ApplicationEventPublisher eventPublisher;

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload,
            @Header("simpSessionAttributes") Map<String, Object> session) {
        
        if ("DEFAULT".equals(ecosystemMemoryStorage.getRoomTheme(roomId))) {
            Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방 없음"));
        
            // 3. 메모리에 등록! (이걸 안 해서 계속 DEFAULT였던 것)
            ecosystemMemoryStorage.registerRoom(roomId, room.getRoomTheme().name());
            log.info("메모리에 테마 등록 완료: {} -> {}", roomId, room.getRoomTheme());
        }

        Long userId = (Long) session.get("USER_ID");

        if (userId == null) {
            System.out.println("경고: 로그인 안 된 유저가 방 입장 시도!");
            return; 
        }

        String theme = ecosystemMemoryStorage.getRoomTheme(roomId);
        ecosystemMemoryStorage.registerRoom(roomId, theme);

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, roomId, null, null, ActionType.ENTER_ROOM));
    }

    @MessageMapping("/ecosystem/{roomId}/interact")
    public void handleCritterInteraction(@DestinationVariable Long roomId,
            @Payload Map<String, Object> payload,
            @Header("simpSessionAttributes") Map<String, Object> session) {
        Long userId = (Long) session.get("USER_ID");

        Long critterId = Long.valueOf(payload.get("critterId").toString());

        List<CritterLocationDto> critters = ecosystemMemoryStorage.getCrittersByRoom(roomId);

        double mouseX = Double.parseDouble(payload.get("mouseX").toString());
        double mouseY = Double.parseDouble(payload.get("mouseY").toString());

        critterService.processInteraction(userId, roomId, critterId, mouseX, mouseY, critters);
    }

    @MessageMapping("/ecosystem/{roomId}/drop-food")
    public void handleDropFood(@DestinationVariable Long roomId,
                               @Payload Map<String, Object> payload,
                               @Header("simpSessionAttributes") Map<String, Object> session) {
        Long userId = (Long) session.get("USER_ID");
        
        // 1. 프론트에서 보낸 좌표와 타입을 확실하게 꺼내기
        double x = Double.parseDouble(payload.get("x").toString());
        double y = Double.parseDouble(payload.get("y").toString());
        String foodTypeStr = payload.get("foodType").toString();
        FoodType foodType = FoodType.valueOf(foodTypeStr); 
        
        foodService.dropFood(userId, roomId, foodType, x, y);
    }
}