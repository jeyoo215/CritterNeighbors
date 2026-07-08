package com.critter.critter_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/chat/{roomId}/send")
    @SendTo("/topic/chat/{roomId}")
    public Map<String, Object> sendMessage(@DestinationVariable Long roomId,
                @Payload Map<String, Object> payload,
                @Header("simpSessionAttributes") Map<String, Object> session) {

        Long userId = (Long) session.get("USER_ID");

        payload.put("senderId", userId); 
        payload.put("timestamp", System.currentTimeMillis());
        return payload;
    }
}