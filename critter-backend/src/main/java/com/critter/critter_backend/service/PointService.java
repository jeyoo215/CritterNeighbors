package com.critter.critter_backend.service;

import org.springframework.stereotype.Service;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.repository.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
    private final AccountRepository accountRepository;

    @Transactional
    public void earn(Long userId, Long amount) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        account.addPoint(amount);
        accountRepository.save(account);
    }

    // 포인트 사용 (상점 입양 등)
    @Transactional
    public void spend(Long userId, Long amount) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        
        log.info("사용자 ID: {} | DB상의 실제 포인트: {} | 입양 가격: {}", userId, account.getPoint(), amount);
        
        if (account.getPoint() < amount) {
            throw new RuntimeException("포인트가 부족합니다!");
        }
        
        account.addPoint(-amount);
        accountRepository.save(account);
    }
}