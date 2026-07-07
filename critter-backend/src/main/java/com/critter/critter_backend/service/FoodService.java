package com.critter.critter_backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.FoodType;
import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.entity.Food;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final EcosystemMemoryStorage memoryStorage;
    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void purchaseFood(Long userId, Long roomId, FoodType type) {
        // 1. 포인트 차감
        eventPublisher.publishEvent(new PointEvents.Spend(userId, 3L, PointReason.BUY_FOOD));

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(
            userId, roomId, null, null, ActionType.BUY_FOOD
        ));
    }

    @Transactional
    public void dropFood(Long userId, Long roomId, FoodType type, double x, double y) {
        String theme = memoryStorage.getRoomTheme(roomId);
        
        Food food = Food.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .x(x)
                .y(y)
                .isSinking("OCEAN".equalsIgnoreCase(theme))
                .build();
        
        memoryStorage.addFood(roomId, food);
        
        Map<String, Object> update = new HashMap<>();
        update.put("critters", memoryStorage.getCrittersByRoom(roomId));
        update.put("foods", memoryStorage.getFoods(roomId));
        messagingTemplate.convertAndSend("/topic/ecosystem/" + roomId, (Object) update);

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(
            userId, roomId, null, null, ActionType.DROP_FOOD
        ));
    }
}