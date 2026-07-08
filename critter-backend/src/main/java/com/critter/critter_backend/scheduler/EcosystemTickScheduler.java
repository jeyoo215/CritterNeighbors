package com.critter.critter_backend.scheduler;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Food;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            List<Food> foods = memoryStorage.getFoods(roomId);

            foods.forEach(f -> f.setEatingCount(0));

            // 먹이 연산
            for (Food food : foods) {
                if (food.getEatingCount() == 0 && food.isSinking()) {
                    if (food.getY() < CANVAS_HEIGHT - 10) {
                        food.setY(food.getY() + 0.3);
                    }
                }
            }

            // 크리터 연산 (추적 및 먹기)
            if (!critters.isEmpty()) {
                for (CritterLocationDto critter : critters) {
                
                    // 크리터의 종족값 가져오기 (호불호 판별용)
                    CritterType typeEnum = null;
                    try {
                        typeEnum = CritterType.valueOf(critter.getCritterType());
                    } catch (IllegalArgumentException e) {
                        continue; 
                    }

                    Food targetFood = null;
                    double minDistance = 600.0;

                    // 자기 주변에 좋아하는 먹이가 있는지 레이더 스캔!
                    for (Food food : foods) {
                        if (typeEnum.likes(food.getType())) {
                            double dist = Math.hypot(food.getX() - critter.getX(), food.getY() - critter.getY());
                            if (dist < minDistance) {
                                minDistance = dist;
                                targetFood = food; // 가장 가까운 좋아하는 먹이를 타겟으로!
                            }
                        }
                    }

                    // 🍔 타겟 먹이가 있고, 패닉 상태가 아니라면?
                    if (targetFood != null && !"PANIC".equals(critter.getStatus())) {
                        if (minDistance < 20.0) {
                            // 냠냠! 먹이에 거의 닿았을 때 (20px 이내)
                            critter.setStatus("EATING");
                            critter.setVx(0.0); // 제자리 정지
                            critter.setVy(0.0);

                            critter.setX(targetFood.getX());
                            critter.setY(targetFood.getY());
                            targetFood.startEating(); // 이 먹이를 먹는 마릿수 +1 증가!
                        } else {
                            // 먹이를 향해 돌격! (CHASING)
                            critter.setStatus("CHASING");
                            double dx = targetFood.getX() - critter.getX();
                            double dy = targetFood.getY() - critter.getY();
                            double chaseSpeed = 3.0;
                            critter.setVx((dx / minDistance) * chaseSpeed);
                            critter.setVy((dy / minDistance) * chaseSpeed);
                            
                            critter.setX(critter.getX() + critter.getVx());
                            critter.setY(critter.getY() + critter.getVy());
                        }
                    } 
                    else {
                        // 먹이가 사라졌는데 아직 EATING 상태면 IDLE로 돌려보냄 (먹이 증발 처리)
                        if ("EATING".equals(critter.getStatus()) || "CHASING".equals(critter.getStatus())) {
                            critter.setStatus("IDLE");
                        }

                        double vMag = Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy());
                        if (vMag > 5.0) { 
                            critter.setVx(critter.getVx() * 0.9); 
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
                                double currentSpeed = Math.max(1.0, Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy()));
                                critter.setVx(Math.cos(newAngle) * currentSpeed);
                                critter.setVy(Math.sin(newAngle) * currentSpeed);
                            }
                            critter.setX(critter.getX() + critter.getVx() * speedMultiplier);
                            critter.setY(critter.getY() + critter.getVy() * speedMultiplier);
                        }
                    }

                    // 🔄 벽 튕기기 (모든 상태 공통)
                    if (critter.getX() <= 20 || critter.getX() >= CANVAS_WIDTH - 20) {
                        critter.setVx(-critter.getVx());
                        critter.setX(Math.max(0, Math.min(critter.getX(), CANVAS_WIDTH)));
                    }
                    if (critter.getY() <= 20 || critter.getY() >= CANVAS_HEIGHT - 20) {
                        critter.setVy(-critter.getVy());
                        critter.setY(Math.max(0, Math.min(critter.getY(), CANVAS_HEIGHT)));
                    }
                }
            }

            foods.removeIf(food -> {
                boolean isTooOld = food.isFinished();
                boolean isEaten = false;
                if (food.getEatingCount() > 0) {
                    double vanishProbability = 0.005 + (food.getEatingCount() * 0.005);
                    isEaten = Math.random() < vanishProbability;
                }
                return isTooOld || isEaten;
            });

            // 프론트엔드로 크리터와 먹이 목록을 한 번에 전송! (DTO 래핑)
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("critters", critters);
            syncData.put("foods", foods);

            messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, (Object) syncData);
            memoryStorage.removeFinishedFoods(roomId);
        }
    }
}