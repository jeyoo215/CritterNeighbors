package com.critter.critter_backend.scheduler;

import com.critter.critter_backend.domain.CreatureStatus;
import com.critter.critter_backend.domain.CreatureType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcosystemTickScheduler {

    private final EcosystemMemoryStorage memoryStorage;
    private final SimpMessagingTemplate messagingTemplate;

    private static final double CANVAS_WIDTH = 800.0;
    private static final double CANVAS_HEIGHT = 600.0;

    @Scheduled(fixedRate = 33)
    public void processEcosystemTick() {
        // 테스트용 더미
        memoryStorage.addSession(1L, "FAKE_SESSION_ID"); 

        if (memoryStorage.getCreaturesByRoom(1L).isEmpty()) {
            java.util.List<CritterLocationDto> dummyList = new java.util.ArrayList<>();
            dummyList.add(new CritterLocationDto(
                100L,
                "홍칠이레서판다",
                CreatureType.RABBIT.name(), // 테스트를 위해 명세서에 있는 타입을 예시로 지정!
                400.0, 300.0,
                CreatureStatus.IDLE.name(),
                2.0, 2.0 
            ));
            memoryStorage.loadCreatures(1L, dummyList);
        }
        // 테스트용 더미

        Set<Long> activeRoomIds = memoryStorage.getActiveRoomIds();
        if (activeRoomIds.isEmpty()) return;

        for (Long roomId : activeRoomIds) {
            List<CritterLocationDto> critters = memoryStorage.getCreaturesByRoom(roomId);
            if (critters.isEmpty()) continue;

            for (CritterLocationDto critter : critters) {
                
                // 🚨 [명세서 스펙 1] 동물 종류(Type)에 따른 이동 속도 가중치 부여 (차별화된 AI)
                double speedMultiplier = 1.0;
                String type = critter.getCreatureType();
                
                if ("RABBIT".equals(type) || "FOX".equals(type)) {
                    speedMultiplier = 2.0; // 토끼나 여우는 엄청 빠름!
                } else if ("TURTLE".equals(type)) {
                    speedMultiplier = 0.5; // 거북이는 느릿느릿
                }

                // 🚨 [명세서 스펙 2] 행동 패턴(Status)에 따른 AI 물리 분기 연산
                // 프론트에서 마우스 접근을 감지해 상태를 PANIC이나 SHELTER로 바꿨다고 가정했을 때의 연산이야.
                if ("PANIC".equals(critter.getStatus())) {
                    // 미쳐 날뛰며 도망침 (속도 3배 증가)
                    critter.setX(critter.getX() + critter.getVx() * speedMultiplier * 3.0);
                    critter.setY(critter.getY() + critter.getVy() * speedMultiplier * 3.0);
                } else if ("SHELTER".equals(critter.getStatus())) {
                    // 정지 후 숨기 패턴 (이동하지 않음)
                    // 좌표 연산을 생격하고 멈춤 상태 유지
                } else {
                    // 일반 IDLE 상태: 지정된 기본 속도 가중치로 이동
                    critter.setX(critter.getX() + critter.getVx() * speedMultiplier);
                    critter.setY(critter.getY() + critter.getVy() * speedMultiplier);
                }

                // 🔄 벽 튕기기 물리 연산 코어
                if (critter.getX() <= 0 || critter.getX() >= CANVAS_WIDTH) {
                    critter.setVx(-critter.getVx());
                    critter.setX(Math.max(0, Math.min(critter.getX(), CANVAS_WIDTH)));
                }
                if (critter.getY() <= 0 || critter.getY() >= CANVAS_HEIGHT) {
                    critter.setVy(-critter.getVy());
                    critter.setY(Math.max(0, Math.min(critter.getY(), CANVAS_HEIGHT)));
                }
            }

            messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, critters);
        }
    }
}