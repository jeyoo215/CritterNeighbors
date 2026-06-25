package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.CreatureStatus;
import com.critter.critter_backend.domain.CreatureType;
import com.critter.critter_backend.entity.Creature; // MySQL 저장용 정적 정보 엔티티 (네 엔티티명 확인!)
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.exception.InvalidHabitatException;
import com.critter.critter_backend.repository.CreatureRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatureService {

    private final CreatureRepository creatureRepository;
    private final EcosystemRepository ecosystemRepository;

    /*
     * 상점 동물 분양 입주 (비즈니스 검증 규칙 포함)
    */
    @Transactional
    public Creature adoptCreature(Long roomId, String creatureName, CreatureType creatureType) {
        // 1. 분양할 방 조회
        Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 생태계 방입니다."));

        // 2. 방의 테마 환경과 동물의 필수 서식지 상성 체크!
        // Enum에 기가 막히게 설계해 둔 getRequiredTheme()를 활용하자!
        if (!room.getRoomTheme().equals(creatureType.getRequiredTheme())) {
            throw new InvalidHabitatException(
                "환경 상성 불일치 : " + room.getRoomTheme() + " 테마 방에는 " + creatureType.name() + "을(를) 분양받을 수 없습니다."
            );
        }

        // 3. 검증 통과 시 MySQL DB에 새 생명체 정적 정보 저장
        Creature creature = new Creature();
        creature.setEcosystem(room); // 대포스트 매핑 확인
        creature.setCreatureName(creatureName);
        creature.setCreatureType(creatureType);
        creature.setStatus(CreatureStatus.IDLE);

        return creatureRepository.save(creature);
    }
}