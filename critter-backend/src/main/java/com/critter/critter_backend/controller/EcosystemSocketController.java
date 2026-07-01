package com.critter.critter_backend.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EcosystemSocketController {

    private final EcosystemMemoryStorage ecosystemMemoryStorage;

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload) {
        
        String nickname = (String) payload.get("nickname");
        System.out.println(roomId + "번 방에 " + nickname + "님이 입장하셨습니다!");
    }

    @MessageMapping("/ecosystem/{roomId}/interact")
    public void handleCritterInteraction(@DestinationVariable Long roomId, @Payload Map<String, Object> payload) {
        Long critterId = Long.valueOf(payload.get("critterId").toString());
        String action = payload.get("action").toString(); // "CLICK"
        double mouseX = Double.parseDouble(payload.get("mouseX").toString());
        double mouseY = Double.parseDouble(payload.get("mouseY").toString());

        List<CritterLocationDto> critters = ecosystemMemoryStorage.getCrittersByRoom(roomId);
        critters.stream().filter(c -> c.getCritterId().equals(critterId)).findFirst().ifPresent(critter -> {
        
            // 1. 패닉 상태 로직: 연속 클릭 시 상태 변경
            critter.setStatus("PANIC");
        
            // 2. 도망가기 로직: 마우스 반대편으로 벡터 즉시 수정
            double dx = critter.getX() - mouseX;
            double dy = critter.getY() - mouseY;
            double dist = Math.sqrt(dx*dx + dy*dy);
        
            // 정규화 후 속도 곱하기
            critter.setVx((dx / dist) * 5.0); 
            critter.setVy((dy / dist) * 5.0);
        });
    }
}