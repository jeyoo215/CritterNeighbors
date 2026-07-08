package com.critter.critter_backend.service;

import com.critter.critter_backend.repository.PointLogRepository;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;

import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.entity.PointLog;
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.repository.EcosystemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointLogRepository pointLogRepository;
    private final AccountRepository accountRepository;
    private final EcosystemRepository ecosystemRepository;

    // 포인트 획득
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void earn(Long userId, Long amount, PointReason reason) {

        accountRepository.addPoint(userId, amount);

        PointLog log = PointLog.builder()
            .accountId(userId)
            .amount(amount)
            .reason(reason)
            .build();
        pointLogRepository.save(log);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void eventEarn(PointEvents.Earn event) {
        earn(event.getUserId(), event.getAmount(), event.getReason());
    }
    

    @Transactional
    public boolean processVisit(Long userId, Long roomId) {
        Ecosystem room = ecosystemRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        if (room.getAccount().getUserId().equals(userId)) {
            return false;
        }

        if (isAlreadyDoneToday(userId, PointReason.DAILY_VISIT_REWARD)) {
            return false;
        }

        earn(userId, 10L, PointReason.DAILY_VISIT_REWARD);
        return true;
    }


    // 포인트 사용
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void spend(Long userId, Long amount, PointReason reason) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        
        if (account.getPoint() < amount) {
            throw new RuntimeException("포인트가 부족합니다!");
        }
        
        account.addPoint(-amount);
        accountRepository.save(account);

        PointLog log = PointLog.builder()
            .accountId(userId)
            .amount(-amount)
            .reason(reason)
            .build();
        pointLogRepository.save(log);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void eventEarn(PointEvents.Spend event) {
        spend(event.getUserId(), event.getAmount(), event.getReason());
    }



    private boolean isAlreadyDoneToday(Long userId, PointReason reason) {
        LocalDateTime now = LocalDateTime.now();
        // 당일 오전 6시를 기준으로 함
        LocalDateTime resetTime = now.withHour(6).withMinute(0).withSecond(0).withNano(0);
        
        // 지금 시간이 6시 이전이라면, 리셋 기준은 어제 6시가 됨
        if (now.isBefore(resetTime)) {
            resetTime = resetTime.minusDays(1);
        }
        
        return pointLogRepository.existsByAccountIdAndReasonAndCreatedAtAfter(userId, reason, resetTime);
    }
}