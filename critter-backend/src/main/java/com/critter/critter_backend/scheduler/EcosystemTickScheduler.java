package com.critter.critter_backend.scheduler;

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

        Set<Long> activeRoomIds = memoryStorage.getActiveRoomIds();
        if (activeRoomIds.isEmpty()) return;

        for (Long roomId : activeRoomIds) {
            List<CritterLocationDto> critters = memoryStorage.getCrittersByRoom(roomId);
            if (critters.isEmpty()) continue;

            for (CritterLocationDto critter : critters) {

                double vMag = Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy());
                if (vMag > 5.0) { // 패닉 속도 제한
                    critter.setVx(critter.getVx() * 0.9); // 감속
                    critter.setVy(critter.getVy() * 0.9);
                }

                if ("IDLE".equals(critter.getStatus()) && Math.random() < 0.007) {
                    critter.setStatus("SHELTER");
                }

                double speedMultiplier = 0.6;
                String type = critter.getCritterType();
                if ("RABBIT".equals(type) || "FOX".equals(type)) {
                    speedMultiplier = 1.0;
                } else if ("TURTLE".equals(type)) {
                    speedMultiplier = 0.5;
                }
                
                if ("SHELTER".equals(critter.getStatus())) {
                    // 확률적으로 다시 IDLE로 복귀 (예: 0.5% 확률로 복귀)
                    if (Math.random() < 0.005) {
                        critter.setStatus("IDLE");
                    }
                }
                else if ("PANIC".equals(critter.getStatus())) {
                    if (Math.random() < 0.20) { 
                        double newAngle = Math.random() * 2 * Math.PI;
                        double panicSpeed = 8.0;
                        critter.setVx(Math.cos(newAngle) * panicSpeed);
                        critter.setVy(Math.sin(newAngle) * panicSpeed);
                    }                        

                    critter.setX(critter.getX() + critter.getVx());
                    critter.setY(critter.getY() + critter.getVy());

                    if (Math.random() < 0.005) {
                        critter.setStatus("IDLE"); 
                    }
                } 
                else {
                    if (Math.random() < 0.01) { 
                        double newAngle = Math.random() * 2 * Math.PI;
                        double currentSpeed = Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy());
                        critter.setVx(Math.cos(newAngle) * Math.max(1.0, currentSpeed));
                        critter.setVy(Math.sin(newAngle) * Math.max(1.0, currentSpeed));
                    }
                    critter.setX(critter.getX() + critter.getVx() * speedMultiplier);
                    critter.setY(critter.getY() + critter.getVy() * speedMultiplier);
                }

                // 벽 튕기기 물리 연산 코어
                if (critter.getX() <= 20 || critter.getX() >= CANVAS_WIDTH-20) {
                    critter.setVx(-critter.getVx());
                    critter.setX(Math.max(0, Math.min(critter.getX(), CANVAS_WIDTH)));
                }
                if (critter.getY() <= 20 || critter.getY() >= CANVAS_HEIGHT-20) {
                    critter.setVy(-critter.getVy());
                    critter.setY(Math.max(0, Math.min(critter.getY(), CANVAS_HEIGHT)));
                }
            }

            messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, critters);
        }
    }
}