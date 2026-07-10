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
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.repository.CritterRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.service.FoodService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EcosystemSocketController {

    private final EcosystemMemoryStorage memoryStorage;
    private final CritterService critterService;
    private final EcosystemRepository ecosystemRepository;
    private final CritterRepository critterRepository;
    private final FoodService foodService;

    private final ApplicationEventPublisher eventPublisher;

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload,
            @Header("simpSessionAttributes") Map<String, Object> session) {
        
        if (memoryStorage.getCrittersByRoom(roomId).isEmpty()) {
            List<Critter> dbCritters = critterRepository.findByEcosystem_RoomId(roomId);
            List<CritterLocationDto> memoryCritters = new ArrayList<>();

            for (Critter c : dbCritters) {
                // c.getStatus().name()으로 DTO(String) 규격에 완벽 일치
                memoryCritters.add(new CritterLocationDto(
                        c.getCritterId(),
                        c.getCritterName(),
                        c.getCritterType().name(), 
                        400.0, 300.0,               
                        c.getStatus().name(), 
                        (Math.random() - 0.5) * 4.0, 
                        (Math.random() - 0.5) * 4.0  
                ));
            }
            
            memoryStorage.loadCritters(roomId, memoryCritters);
        }

        if ("DEFAULT".equals(memoryStorage.getRoomTheme(roomId))) {
            Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방이 없음!"));
        
            // 메모리에 등록
            memoryStorage.registerRoom(roomId, room.getRoomTheme().name());
        }

        Long userId = (Long) session.get("USER_ID");

        if (userId == null) {
            System.out.println("경고: 로그인 안 된 유저가 방 입장 시도!");
            return; 
        }

        String theme = memoryStorage.getRoomTheme(roomId);
        memoryStorage.registerRoom(roomId, theme);

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, roomId, null, null, ActionType.ENTER_ROOM));
    }

    @MessageMapping("/ecosystem/{roomId}/interact")
    public void handleCritterInteraction(@DestinationVariable Long roomId,
            @Payload Map<String, Object> payload,
            @Header("simpSessionAttributes") Map<String, Object> session) {
        Long userId = (Long) session.get("USER_ID");

        Long critterId = Long.valueOf(payload.get("critterId").toString());

        List<CritterLocationDto> critters = memoryStorage.getCrittersByRoom(roomId);

        double mouseX = Double.parseDouble(payload.get("mouseX").toString());
        double mouseY = Double.parseDouble(payload.get("mouseY").toString());

        critterService.processInteraction(userId, roomId, critterId, mouseX, mouseY, critters);
    }

    @MessageMapping("/ecosystem/{roomId}/drop-food")
    public void handleDropFood(@DestinationVariable Long roomId,
                               @Payload Map<String, Object> payload,
                               @Header("simpSessionAttributes") Map<String, Object> session) {
        Long userId = (Long) session.get("USER_ID");
        
        // 프론트에서 보낸 좌표와 타입을 확실하게 꺼내기
        double x = Double.parseDouble(payload.get("x").toString());
        double y = Double.parseDouble(payload.get("y").toString());
        String foodTypeStr = payload.get("foodType").toString();
        FoodType foodType = FoodType.valueOf(foodTypeStr); 
        
        foodService.dropFood(userId, roomId, foodType, x, y);
    }
}