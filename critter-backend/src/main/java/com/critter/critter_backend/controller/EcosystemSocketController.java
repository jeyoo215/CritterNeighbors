package com.critter.critter_backend.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.service.CritterService;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EcosystemSocketController {

    private final EcosystemMemoryStorage ecosystemMemoryStorage;
    private final CritterService critterService;

    private final ApplicationEventPublisher eventPublisher;

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload,
            @Header("simpSessionAttributes") Map<String, Object> session) {
        
        Long userId = (Long) session.get("USER_ID");
        // String nickname = (String) payload.get("nickname");

        if (userId == null) {
            System.out.println("경고: 로그인 안 된 유저가 방 입장 시도!");
            return; 
        }

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
}