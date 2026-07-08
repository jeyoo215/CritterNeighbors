package com.critter.critter_backend.storage;

import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Food;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class EcosystemMemoryStorage {

    private final Map<Long, List<CritterLocationDto>> roomCritterMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> roomSessionMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToRoomMap = new ConcurrentHashMap<>();

    public void addSession(Long roomId, String sessionId) {
        roomSessionMap.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        sessionToRoomMap.put(sessionId, roomId);
    }

    public void removeSession(Long roomId, String sessionId) {
        Set<String> sessions = roomSessionMap.get(roomId);
        if (sessions != null) {
            sessions.remove(sessionId);
            sessionToRoomMap.remove(sessionId); 
            
            if (sessions.isEmpty()) {
                roomSessionMap.remove(roomId);
            }
        }
    }

    public boolean hasPassengers(Long roomId) {
        Set<String> sessions = roomSessionMap.get(roomId);
        return sessions != null && !sessions.isEmpty();
    }

    public Set<Long> getActiveRoomIds() {
        return roomSessionMap.keySet();
    }

    public Long getRoomIdBySession(String sessionId) {
        return sessionToRoomMap.get(sessionId);
    }

    private final Map<Long, String> roomThemes = new ConcurrentHashMap<>();

    public void registerRoom(Long roomId, String theme) {
        roomThemes.put(roomId, theme);
    }

    public String getRoomTheme(Long roomId) {
        return roomThemes.getOrDefault(roomId, "DEFAULT");
    }

    // 🟢 여기도 CritterLocationDto로 받아주기!
    public void loadCritters(Long roomId, List<CritterLocationDto> critters) {
        roomCritterMap.put(roomId, new ArrayList<>(critters));
    }

    public List<CritterLocationDto> getCrittersByRoom(Long roomId) {
        return roomCritterMap.getOrDefault(roomId, new ArrayList<>());
    }

    public void unloadRoom(Long roomId) {
        roomCritterMap.remove(roomId);
    }

    public void addCritter(Long roomId, CritterLocationDto critter) {
        // 1. 방에 있는 동물 리스트를 가져와 (없으면 새로 만들어)
        List<CritterLocationDto> critters = roomCritterMap.computeIfAbsent(roomId, k -> new ArrayList<>());
    
        // 2. 그 리스트에  투입
        critters.add(critter);
        log.info("메모리 저장소: {}번 방에 생명체 투입 성공!", roomId);
    }



    // 먹이
    private final Map<Long, List<Food>> roomFoods = new ConcurrentHashMap<>();

    public void addFood(Long roomId, Food food) {
        roomFoods.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(food);
    }

    public List<Food> getFoods(Long roomId) {
        return roomFoods.getOrDefault(roomId, new CopyOnWriteArrayList<>());
    }

    public void removeFinishedFoods(Long roomId) {
        List<Food> foods = roomFoods.get(roomId);
        if (foods != null) {
            foods.removeIf(Food::isFinished);
        }
    }
}