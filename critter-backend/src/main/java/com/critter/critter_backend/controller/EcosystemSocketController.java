package com.critter.critter_backend.controller;

import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.storage.EcosystemMemoryStorage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class EcosystemSocketController {

    private final EcosystemMemoryStorage memoryStorage;

    public EcosystemSocketController(EcosystemMemoryStorage memoryStorage) {
        this.memoryStorage = memoryStorage;
    }

    @MessageMapping("/ecosystem/{roomId}/join")
    public void handleUserJoin(
            @DestinationVariable("roomId") Long roomId, 
            @Payload Map<String, Object> payload) {
        
        String nickname = (String) payload.get("nickname");
        System.out.println(roomId + "번 방에 " + nickname + "님이 입장하셨습니다!");

        // "특정 방"이 비어있는지 콕 집어서 확인 가능
        if (!memoryStorage.hasPassengers(roomId)) {
            System.out.println(roomId + "번 방이 비어있으므로 테스트용 래서판다를 소환합니다!");
            
            // 🟢 속도 vx(1.0), vy(1.0)를 맨 뒤에 추가하여 8개 필드 생성자로 맞춤!
            CritterLocationDto testCritter = new CritterLocationDto(
                100L,                          // critterId
                "홍칠이레서판다",                 // name
                com.critter.critter_backend.domain.CritterType.REDPANDA.name(), // species
                400.0,                         // x
                300.0,                         // y
               "IDLE",                        // status
                1.0,                           // vx
                1.0                            // vy
            );
            
            // 방에다가 리스트 형태로 동물을 적재하는 방식
            java.util.List<CritterLocationDto> initialCritters = new java.util.ArrayList<>();
            initialCritters.add(testCritter);

            memoryStorage.loadCritters(roomId, initialCritters);
        }
    }
}