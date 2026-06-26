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
        Long roomId = (roomIdStr != null) ? Long.valueOf(roomIdStr) : 1L;

        memoryStorage.addSession(roomId, sessionId);
        log.info("🔌 [소켓 연결] 방 번호: {}, 세션 ID: {}", roomId, sessionId);

        if (memoryStorage.getCrittersByRoom(roomId).isEmpty()) {
            List<Critter> dbCritters = critterRepository.findByEcosystem_RoomId(roomId);
            List<CritterLocationDto> memoryCritters = new ArrayList<>();

            for (Critter c : dbCritters) {
                // 🟢 [에러 진압 1] c.getStatus() 뒤에 .name()을 붙여 DTO(String) 규격에 완벽 일치!
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
            log.info("💾 [On-Demand 로딩] DB에서 {}마리의 동물을 방 {}번 메모리에 적재 완료!", memoryCritters.size(), roomId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // 임시 테스트용 1번 방 지정 (추후 세션별 방 매핑 필요)
        Long roomId = 1L; 
        
        // 🟢 [에러 진압 2] 네 기존 매개변수 규격 (roomId, sessionId)에 맞춰서 호출!
        memoryStorage.removeSession(roomId, sessionId); 
        log.info("❌ [소켓 단절] 방 번호: {}, 세션 ID: {}", roomId, sessionId);

        // 🟢 [에러 진압 3] 네 순정 메서드 !hasPassengers(roomId) 로 비어있는지 체크!
        if (!memoryStorage.hasPassengers(roomId)) {
            List<CritterLocationDto> memoryCritters = memoryStorage.getCrittersByRoom(roomId);

            for (CritterLocationDto dto : memoryCritters) {
                critterRepository.findById(dto.getCritterId()).ifPresent(critter -> {
                    critter.setStatus(CritterStatus.valueOf(dto.getStatus())); 
                    critterRepository.save(critter);
                });
            }

            // 🟢 [에러 진압 4] 네 순정 메서드 unloadRoom(roomId) 호출해서 메모리 청소!
            memoryStorage.unloadRoom(roomId);
            log.info("♻️ [동적 메모리 해제] 방 {}번에 접속자가 없어 영속화 후 언로드 완료!", roomId);
        }
    }
}