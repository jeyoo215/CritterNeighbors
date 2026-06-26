package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.CritterStatus;
import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.entity.Critter; // MySQL 저장용 정적 정보 엔티티 (네 엔티티명 확인!)
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.exception.InvalidHabitatException;
import com.critter.critter_backend.repository.CritterRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CritterService {

    private final CritterRepository critterRepository;
    private final EcosystemRepository ecosystemRepository;

    /*
     * 상점 동물 분양 입주 (비즈니스 검증 규칙 포함)
    */
    @Transactional
    public Critter adoptCritter(Long roomId, String critterName, CritterType critterType) {
        // 1. 분양할 방 조회
        Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 생태계 방입니다."));

        // 2. 방의 테마 환경과 동물의 필수 서식지 상성 체크!
        // Enum에 기가 막히게 설계해 둔 getRequiredTheme()를 활용하자!
        if (!room.getRoomTheme().equals(critterType.getRequiredTheme())) {
            throw new InvalidHabitatException(
                "환경 상성 불일치 : " + room.getRoomTheme() + " 테마 방에는 " + critterType.name() + "을(를) 분양받을 수 없습니다."
            );
        }

        // 3. 검증 통과 시 MySQL DB에 새 생명체 정적 정보 저장
        Critter critter = new Critter();
        critter.setEcosystem(room); // 대포스트 매핑 확인
        critter.setCritterName(critterName);
        critter.setCritterType(critterType);
        critter.setStatus(CritterStatus.IDLE);

        return critterRepository.save(critter);
    }
}