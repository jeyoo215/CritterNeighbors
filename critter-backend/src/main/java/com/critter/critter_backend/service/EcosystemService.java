package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.EcosystemTheme; // 🟢 도메인 패키지의 테마 Enum
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Ecosystem; // 🟢 네 방 엔티티 이름 확인! (ECOSYSTEMS)
import com.critter.critter_backend.repository.AccountRepository; // 💡 이제 절대 안 틀림! 완벽 매핑!
import com.critter.critter_backend.repository.EcosystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EcosystemService {

    private final EcosystemRepository ecosystemRepository;
    private final AccountRepository accountRepository;

    /**
     * 🟢 [명세서 스펙] 유저별 독립된 생태계 방 개설
     */
    @Transactional
    public Ecosystem createRoom(Long userId, String roomName, String themeStr) {
        // 1. 방을 개설할 유저 검증
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (account.getPoint() < 50) {
            throw new RuntimeException("포인트가 부족해요!");
        }

        // 2. [명세서 스펙] OCEAN, FOREST, GRASSLAND 중 하나의 환경 테마 필수로 선택 검증
        EcosystemTheme theme;
        try {
            theme = EcosystemTheme.valueOf(themeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 환경 테마입니다. (OCEAN, FOREST, GRASSLAND 중 선택)");
        }

        // 3. 방 엔티티 빌드 및 저장
        Ecosystem ecosystem = new Ecosystem();
        ecosystem.setAccount(account); // 네 연관관계 필드명 확인!
        ecosystem.setRoomName(roomName);
        ecosystem.setRoomTheme(theme); // Enum 세팅

        account.setPoint(account.getPoint() - 50);

        return ecosystemRepository.save(ecosystem);
    }

    /**
     * 🟢 [명세서 스펙] 특정 유저의 고유 방 조회
     */
    @Transactional(readOnly = true)
    public List<Ecosystem> getRoomsByUserId(Long userId) {
        return ecosystemRepository.findByAccount_UserId(userId); // 네 연관관계 필드명에 맞게 매핑!
    }
}