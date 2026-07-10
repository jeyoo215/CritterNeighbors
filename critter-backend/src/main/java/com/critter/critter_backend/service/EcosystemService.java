package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.EcosystemTheme;
import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Ecosystem; 
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.exception.ResourceNotFoundException;
import com.critter.critter_backend.exception.habitat.HabitatNotFoundException;
import com.critter.critter_backend.exception.point.NoPointException;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EcosystemService {

    private final EcosystemRepository ecosystemRepository;
    private final AccountRepository accountRepository;

    private final ApplicationEventPublisher eventPublisher;

    // 유저 별 생태계 생성
    @Transactional
    public Ecosystem createRoom(Long userId, String roomName, String themeStr) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 유저입니다."));

        List<Ecosystem> existingRooms = ecosystemRepository.findByAccount_UserId(userId);
        boolean isFirstRoom = existingRooms.isEmpty();

        Long price = isFirstRoom ? 0L : 50L;

        if (account.getPoint() < price) {
            throw new NoPointException("포인트가 부족합니다.");
        }

        EcosystemTheme theme;
        try {
            theme = EcosystemTheme.valueOf(themeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HabitatNotFoundException("올바르지 않은 환경 테마입니다.");
        }

        Ecosystem ecosystem = new Ecosystem();
        ecosystem.setAccount(account);
        ecosystem.setRoomName(roomName);
        ecosystem.setRoomTheme(theme);
        Ecosystem newEcosystem = ecosystemRepository.save(ecosystem);

        eventPublisher.publishEvent(new PointEvents.Spend(userId, price, PointReason.ADOPT_CRITTER));

        return newEcosystem;
    }


    // 내 방
    @Transactional(readOnly = true)
    public List<Ecosystem> getRoomsByUserId(Long userId) {
        return ecosystemRepository.findByAccount_UserId(userId);
    }


    // 남의 방
    @Transactional(readOnly = true)
    public List<Ecosystem> getRandomRoomsExcludingUser(Long userId) {
        // 레포지토리에서 쿼리로 뽑아온 5개를 그대로 반환
        return ecosystemRepository.findRandomRoomsExcludingUser(userId); 
    }
}