package com.critter.critter_backend.scheduler;

import com.critter.critter_backend.domain.CritterStatus;
import com.critter.critter_backend.domain.CritterType;
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
        memoryStorage.addSession(1L, "FAKE_SESSION_1");
        memoryStorage.addSession(2L, "FAKE_SESSION_2");

        if (memoryStorage.getCrittersByRoom(2L).isEmpty()) {
            java.util.List<CritterLocationDto> dummyList = new java.util.ArrayList<>();
            dummyList.add(new CritterLocationDto(
                100L,
                "야호",
                CritterType.DOG.name(), // 테스트를 위해 명세서에 있는 타입을 예시로 지정!
                400.0, 300.0,
                CritterStatus.IDLE.name(),
                1.0, 1.0
            ));
            memoryStorage.loadCritters(2L, dummyList);
        }
        // 테스트용 더미

        Set<Long> activeRoomIds = memoryStorage.getActiveRoomIds();
        if (activeRoomIds.isEmpty()) return;

        for (Long roomId : activeRoomIds) {
            List<CritterLocationDto> critters = memoryStorage.getCrittersByRoom(roomId);
            if (critters.isEmpty()) continue;

            for (CritterLocationDto critter : critters) {
                
                if (Math.random() < 0.01) { 
                    double newAngle = Math.random() * 2 * Math.PI;
                    double currentSpeed = Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy());
                    critter.setVx(Math.cos(newAngle) * currentSpeed);
                    critter.setVy(Math.sin(newAngle) * currentSpeed);
                }

                // 동물 종류(Type)에 따른 이동 속도 가중치 부여 (차별화된 AI)
                double speedMultiplier = 1.0;
                String type = critter.getCritterType();
                
                if ("RABBIT".equals(type) || "FOX".equals(type)) {
                    speedMultiplier = 2.0; 
                } else if ("TURTLE".equals(type)) {
                    speedMultiplier = 0.5; 
                }

                // 행동 패턴(Status)에 따른 AI 물리 분기 연산
                // 프론트에서 마우스 접근을 감지해 상태를 PANIC이나 SHELTER로 바꿨다고 가정했을 때의 연산이야.
                if ("PANIC".equals(critter.getStatus())) {
                    critter.setX(critter.getX() + critter.getVx() * speedMultiplier * 2.0);
                    critter.setY(critter.getY() + critter.getVy() * speedMultiplier * 2.0);
                } else if ("SHELTER".equals(critter.getStatus())) {
                    // 정지 후 숨기 패턴 (이동하지 않음)
                } else {
                    // 일반 IDLE 상태: 지정된 기본 속도 가중치로 이동
                    critter.setX(critter.getX() + critter.getVx() * speedMultiplier);
                    critter.setY(critter.getY() + critter.getVy() * speedMultiplier);
                }

                // 🔄 벽 튕기기 물리 연산 코어
                if (critter.getX() <= 10 || critter.getX() >= CANVAS_WIDTH-10) {
                    critter.setVx(-critter.getVx());
                    critter.setX(Math.max(0, Math.min(critter.getX(), CANVAS_WIDTH)));
                }
                if (critter.getY() <= 10 || critter.getY() >= CANVAS_HEIGHT-10) {
                    critter.setVy(-critter.getVy());
                    critter.setY(Math.max(0, Math.min(critter.getY(), CANVAS_HEIGHT)));
                }
            }

            messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, critters);
        }
    }
}