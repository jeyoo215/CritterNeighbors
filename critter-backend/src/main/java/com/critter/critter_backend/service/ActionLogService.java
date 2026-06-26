package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.ActionLog;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.repository.ActionLogRepository;
import com.critter.critter_backend.repository.CritterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final CritterRepository critterRepository;

    /*
     * 동물의 상태 변화 및 행동 로그 비비 비동기 적재
     * @Async가 붙으면 이 메서드는 메인 소켓 스레드가 아닌, 별도의 Thread Pool에서 독립적으로 실행됨!
    */
    @Async
    @Transactional
    public void recordActionLog(Long critterId, String actionType) {
        // 1. 대상 생명체 조회
        Critter critter = critterRepository.findById(critterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 생명체입니다."));

        // 2. 로그 엔티티 빌드 및 저장
        ActionLog actionLog = new ActionLog();
        actionLog.setCritter(critter); // 네 연관관계 필드명 확인!
        actionLog.setActionType(actionType); // ex: "MOUSE_DETECTED", "WALL_COLLIDED" 등

        actionLogRepository.save(actionLog);
        
        log.info("💾 [비동기 로그 적재 완료] 생명체 ID: {}, 행동: {}, 실행 스레드: {}", 
                critterId, actionType, Thread.currentThread().getName());
    }
}