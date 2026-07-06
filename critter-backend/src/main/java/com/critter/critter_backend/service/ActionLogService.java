package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.LogTargetType;
import com.critter.critter_backend.entity.ActionLog;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.repository.ActionLogRepository;
import com.critter.critter_backend.repository.BoardRepository;
import com.critter.critter_backend.repository.CommentRepository;
import com.critter.critter_backend.repository.CritterRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import com.critter.critter_backend.repository.GuestbookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final EcosystemRepository ecosystemRepository;
    private final CritterRepository critterRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final GuestbookRepository guestbookRepository;
    

    /*
        동물의 상태 변화 및 행동 로그 비비 비동기 적재
        @Async가 붙으면 이 메서드는 메인 소켓 스레드가 아닌, 별도의 Thread Pool에서 독립적으로 실행됨!
    */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordActionLog(Long userId, Long roomId, Long targetId, LogTargetType targetType, ActionType actionType) {
        if (targetId != null && targetType != null) {
            validateTargetExists(targetId, targetType); 
        }
        
        ActionLog actionLog = ActionLog.builder()
                .accountId(userId)
                .roomId(roomId)
                .actionType(actionType)
                .targetId(targetId)
                .targetType(targetType)
                .createdAt(LocalDateTime.now())
                .build();
        
        actionLogRepository.save(actionLog);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void eventRecordActionLog(ActionLogEvents.recordActionLog event) {
        recordActionLog(event.getUserId(), event.getRoomId(), event.getTargetId(), event.getTargetType(), event.getActionType());
    }

    public void validateTargetExists(Long targetId, LogTargetType targetType) {
        if (targetId == null) return;

        if (targetType == LogTargetType.CRITTER) {
            critterRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크리터입니다."));
        } else if (targetType == LogTargetType.BOARD) {
            boardRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        } else if (targetType == LogTargetType.COMMENT) {
            commentRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        } else if (targetType == LogTargetType.GUESTBOOK) {
            guestbookRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록입니다."));
        } else if (targetType == LogTargetType.ROOM) {
            ecosystemRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        } else {
            throw new IllegalArgumentException("지원하지 않는 대상입니다.");
        }
    }
}