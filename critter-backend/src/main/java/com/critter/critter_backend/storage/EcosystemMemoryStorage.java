package com.critter.critter_backend.storage;

import com.critter.critter_backend.dto.CritterLocationDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class EcosystemMemoryStorage {

    // 🟢 통합된 CritterLocationDto 리스트를 사용하도록 수정!
    private final Map<Long, List<CritterLocationDto>> roomCreatureMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> roomSessionMap = new ConcurrentHashMap<>();

    public void addSession(Long roomId, String sessionId) {
        roomSessionMap.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
    }

    public void removeSession(Long roomId, String sessionId) {
        Set<String> sessions = roomSessionMap.get(roomId);
        if (sessions != null) {
            sessions.remove(sessionId);
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

    // 🟢 여기도 CritterLocationDto로 받아주기!
    public void loadCreatures(Long roomId, List<CritterLocationDto> creatures) {
        roomCreatureMap.put(roomId, new ArrayList<>(creatures));
    }

    public List<CritterLocationDto> getCreaturesByRoom(Long roomId) {
        return roomCreatureMap.getOrDefault(roomId, new ArrayList<>());
    }

    public void unloadRoom(Long roomId) {
        roomCreatureMap.remove(roomId);
        System.out.println(roomId + "번 방에 접속자가 없어 메모리에서 언로드되었습니다.");
    }

    // EcosystemMemoryStorage.java 안에 추가해줘
    public void addCreature(Long roomId, CritterLocationDto critter) {
        // 1. 방에 있는 동물 리스트를 가져와 (없으면 새로 만들어)
        List<CritterLocationDto> creatures = roomCreatureMap.computeIfAbsent(roomId, k -> new ArrayList<>());
    
        // 2. 그 리스트에 펭귄 투입!
        creatures.add(critter);
        log.info("메모리 저장소: {}번 방에 생명체 투입 성공!", roomId);
    }
}