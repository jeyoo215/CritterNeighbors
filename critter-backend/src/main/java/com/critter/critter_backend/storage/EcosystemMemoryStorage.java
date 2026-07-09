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
    // 실시간 통신을 위한 메모리 저장소

    private final Map<Long, List<CritterLocationDto>> roomCritterMap = new ConcurrentHashMap<>();
    // 크리터의 좌표 정보 저장
    private final Map<Long, Set<String>> roomSessionMap = new ConcurrentHashMap<>();
    // 방에 접속한 유저 세션 정보 (방->유저)
    private final Map<String, Long> sessionToRoomMap = new ConcurrentHashMap<>();
    // 유저가 어떤 방에 접속했는지에 대한 정보 (유저->방)
    private final Map<Long, String> roomThemes = new ConcurrentHashMap<>();
    // 방 테마 정보
    private final Map<Long, List<Food>> roomFoods = new ConcurrentHashMap<>();
    // 방에 있는 먹이 정보

    /*
        * ConcurrentHashMap
        - 일반 HashMap의 경우 멀티스레드 환경에서 동시성 제어가 되지 않음(write와 read 동시 진행 시 데이터 훼손 위험)
        - 멀티스레드로 동작해야 하므로 동시에 접근이 가능한 ConcurrentHashMap 사용
        +) SynchronizedMap이나 Hashtable의 경우도 동시 접근 가능하나 맵 전체에 락을 걸어 성능 병목 우려
           ConcurrentHashMap은 분할 제어 (락 분할?)
    */

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

    public void registerRoom(Long roomId, String theme) {
        roomThemes.put(roomId, theme);
    }

    public String getRoomTheme(Long roomId) {
        return roomThemes.getOrDefault(roomId, "DEFAULT");
    }

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
        List<CritterLocationDto> critters = roomCritterMap.computeIfAbsent(roomId, k -> new ArrayList<>());
        critters.add(critter);
    }


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