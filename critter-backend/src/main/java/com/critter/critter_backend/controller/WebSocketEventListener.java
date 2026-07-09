package com.critter.critter_backend.controller;

import com.critter.critter_backend.storage.EcosystemMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final EcosystemMemoryStorage memoryStorage;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String roomIdStr = headerAccessor.getFirstNativeHeader("roomId");
        
        if (roomIdStr == null) {
            return;
        }

        Long roomId = Long.valueOf(roomIdStr);
        memoryStorage.addSession(roomId, sessionId);
    }

    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Long roomId = memoryStorage.getRoomIdBySession(sessionId);
        
        if (roomId == null) {
            return;
        }

        memoryStorage.removeSession(roomId, sessionId);

        if (!memoryStorage.hasPassengers(roomId)) {
            memoryStorage.unloadRoom(roomId);
        }
    }
}