package com.critter.critter_backend.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class EcosystemSocketController {

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload) {
        
        String nickname = (String) payload.get("nickname");
        System.out.println(roomId + "번 방에 " + nickname + "님이 입장하셨습니다!");
    }
}