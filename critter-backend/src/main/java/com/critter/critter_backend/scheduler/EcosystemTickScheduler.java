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

            // 크리터 연산
            if (!critters.isEmpty()) {
                for (CritterLocationDto critter : critters) {
                
                    // 크리터 타입 겟
                    // 먹이 호불호
                    CritterType typeEnum = null;
                    try {
                        typeEnum = CritterType.valueOf(critter.getCritterType());
                    } catch (IllegalArgumentException e) {
                        continue; 
                    }

                    Food targetFood = null;
                    double minDistance = 600.0;

                    // 좋아하는 먹이가 있으면
                    for (Food food : foods) {
                        if (typeEnum.likes(food.getType())) {
                            double dist = Math.hypot(food.getX() - critter.getX(), food.getY() - critter.getY());
                            if (dist < minDistance) {
                                minDistance = dist;
                                targetFood = food;
                                // 좋아하는 먹이 중 가장 가까운 먹이
                            }
                        }
                    }

                    // 타겟 먹이가 있고, 패닉 상태가 아니면 이동
                    if (targetFood != null && !"PANIC".equals(critter.getStatus())) {
                        if (minDistance < 20.0) {
                            // 먹이랑 가까워지면 먹기 시작
                            critter.setStatus("EATING");
                            critter.setVx(0.0); // 제자리 정지
                            critter.setVy(0.0);

                            critter.setX(targetFood.getX());
                            critter.setY(targetFood.getY());
                            targetFood.startEating(); // 이 먹이를 먹는 마릿수 +1 증가
                        } else {
                            // 먹이랑 거리가 멀면 먹이로 달려감
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
                    // 그 외 일반 크리터 이동 연산
                    else {
                        // 먹이가 사라지면 IDLE로 상태 변경
                        if ("EATING".equals(critter.getStatus()) || "CHASING".equals(critter.getStatus())) {
                            critter.setStatus("IDLE");
                        }

                        // 속도 제한
                        // 서서히 감속 (갑자기 멈춤 방지)
                        double vMag = Math.sqrt(critter.getVx() * critter.getVx() + critter.getVy() * critter.getVy());
                        if (vMag > 5.0) { 
                            critter.setVx(critter.getVx() * 0.9); 
                            critter.setVy(critter.getVy() * 0.9);
                        }

                        // 랜덤으로 숨기+찌그러지기 연산
                        if ("IDLE".equals(critter.getStatus()) && Math.random() < 0.007) {
                            critter.setStatus("SHELTER");
                        }

                        // 크리터 타입 별 속도 차이
                        double speedMultiplier = 0.6;
                        String type = critter.getCritterType();
                        if ("RABBIT".equals(type) || "FOX".equals(type)) {
                            speedMultiplier = 1.0;
                        } else if ("TURTLE".equals(type)) {
                            speedMultiplier = 0.5;
                        }
                        
                        // 움직임 없애기 + 랜덤으로 IDLE 상태로 복귀
                        if ("SHELTER".equals(critter.getStatus())) {
                            if (Math.random() < 0.005) {
                                critter.setStatus("IDLE");
                            }
                        }
                        else if ("PANIC".equals(critter.getStatus())) {
                            // 랜덤으로 방향 바꾸기 + 속도 증가
                            if (Math.random() < 0.20) { 
                                double newAngle = Math.random() * 2 * Math.PI;
                                double panicSpeed = 8.0;
                                critter.setVx(Math.cos(newAngle) * panicSpeed);
                                critter.setVy(Math.sin(newAngle) * panicSpeed);
                            }                        
                            critter.setX(critter.getX() + critter.getVx());
                            critter.setY(critter.getY() + critter.getVy());
                            // 랜덤으로 IDLE 상태 복귀
                            if (Math.random() < 0.005) {
                                critter.setStatus("IDLE"); 
                            }
                        } 
                        // 아무 상태도 아닌 경우 랜덤으로 방향을 바꾸되 속도 올리지 않음 침착크리터 기특해요
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

                    // 벽을 만나면 튕김
                    double speedVariation = 0.8 + Math.random() * 0.4; // 0.8 ~ 1.2 배수의 속도 변화

                    // X축 벽 충돌 체크
                    if (critter.getX() <= 20) {
                        critter.setVx(Math.abs(critter.getVx()) * speedVariation); // 오른쪽으로 이동 + 속도 조정
                        critter.setX(21.0); // 벽 밖으로
                    } else if (critter.getX() >= CANVAS_WIDTH - 20) {
                        critter.setVx(-Math.abs(critter.getVx()) * speedVariation); // 왼쪽으로 이동 + 속도 조정
                        critter.setX(CANVAS_WIDTH - 21); // 벽 밖으로
                    }

                    // Y축 벽 충돌 체크
                    if (critter.getY() <= 20) {
                        critter.setVy(Math.abs(critter.getVy()) * speedVariation); // 아래로 이동 + 속도 조정
                        critter.setY(21.0); // 벽 밖으로
                    } else if (critter.getY() >= CANVAS_HEIGHT - 20) {
                        critter.setVy(-Math.abs(critter.getVy()) * speedVariation); // 위로 이동 + 속도 조정
                        critter.setY(CANVAS_HEIGHT - 21); // 벽 밖으로
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

            // 프론트엔드로 크리터와 먹이 목록을 한 번에 전송
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("critters", critters);
            syncData.put("foods", foods);

            messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, (Object) syncData);
            memoryStorage.removeFinishedFoods(roomId);
        }
    }
}