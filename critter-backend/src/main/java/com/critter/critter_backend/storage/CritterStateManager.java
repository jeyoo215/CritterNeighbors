package com.critter.critter_backend.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CritterStateManager {
    // roomId:critterId -> 남은 시간
    private final Map<String, Integer> shelterTimers = new ConcurrentHashMap<>();

    public void startShelter(Long roomId, Long critterId, int duration) {
        shelterTimers.put(roomId + ":" + critterId, duration);
    }

    public boolean isSheltering(Long roomId, Long critterId) {
        return shelterTimers.containsKey(roomId + ":" + critterId);
    }

    public void tick(Long roomId, Long critterId) {
        String key = roomId + ":" + critterId;
        if (shelterTimers.containsKey(key)) {
            int timeLeft = shelterTimers.get(key) - 1;
            if (timeLeft <= 0) {
                shelterTimers.remove(key);
            } else {
                shelterTimers.put(key, timeLeft);
            }
        }
    }
}
