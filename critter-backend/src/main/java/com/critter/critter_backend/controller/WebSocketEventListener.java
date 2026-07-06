package com.critter.critter_backend.controller;

import com.critter.critter_backend.domain.CritterStatus;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.repository.CritterRepository;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final EcosystemMemoryStorage memoryStorage;
    private final CritterRepository critterRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        String roomIdStr = headerAccessor.getFirstNativeHeader("roomId");
        
        if (roomIdStr == null) {
            log.warn("[경고] 방 번호(roomId) 없이 소켓 연결 시도! 세션ID: {}", sessionId);
            return;
        }

        Long roomId = Long.valueOf(roomIdStr);
        memoryStorage.addSession(roomId, sessionId);
        log.info("[소켓 연결] 방 번호: {}, 세션 ID: {}", roomId, sessionId);

        if (memoryStorage.getCrittersByRoom(roomId).isEmpty()) {
            List<Critter> dbCritters = critterRepository.findByEcosystem_RoomId(roomId);
            List<CritterLocationDto> memoryCritters = new ArrayList<>();

            for (Critter c : dbCritters) {
                // c.getStatus() 뒤에 .name()을 붙여 DTO(String) 규격에 완벽 일치!
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

        // !hasPassengers(roomId) : 방이 비어있는지
        if (!memoryStorage.hasPassengers(roomId)) {
            List<CritterLocationDto> memoryCritters = memoryStorage.getCrittersByRoom(roomId);

            for (CritterLocationDto dto : memoryCritters) {
                critterRepository.findById(dto.getCritterId()).ifPresent(critter -> {
                    critter.setStatus(CritterStatus.valueOf(dto.getStatus())); 
                    critterRepository.save(critter);
                });
            }

            // unloadRoom(roomId) 호출해서 메모리 청소
            memoryStorage.unloadRoom(roomId);
        }
    }
}