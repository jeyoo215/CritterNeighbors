package com.critter.critter_backend.service;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.CritterStatus;
import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.domain.LogTargetType;
import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.dto.CritterLocationDto;
import com.critter.critter_backend.entity.Critter;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.exception.ResourceNotFoundException;
import com.critter.critter_backend.exception.account.ForbiddenException;
import com.critter.critter_backend.exception.habitat.InvalidHabitatException;
import com.critter.critter_backend.repository.CritterRepository;
import com.critter.critter_backend.repository.EcosystemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CritterService {

    private final CritterRepository critterRepository;
    private final EcosystemRepository ecosystemRepository;

    private final ApplicationEventPublisher eventPublisher;

    // 동물 분양
    @Transactional
    public Critter adoptCritter(Long userId, Long roomId, Long price, String critterName, CritterType critterType) {
        eventPublisher.publishEvent(new PointEvents.Spend(userId, price, PointReason.ADOPT_CRITTER));

        Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 생태계 방입니다."));

        if (!room.getRoomTheme().equals(critterType.getRequiredTheme())) {
            throw new InvalidHabitatException("환경 상성 불일치");
        }

        // 검증 통과 시 MySQL DB에 새 생명체 정적 정보 저장
        Critter critter = new Critter();
        critter.setEcosystem(room);
        critter.setCritterName(critterName);
        critter.setCritterType(critterType);
        critter.setStatus(CritterStatus.IDLE);
        Critter adoptCritter = critterRepository.save(critter);

        if (!critter.getEcosystem().getAccount().getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 방에서만 크리터를 입양할 수 있습니다.");
        }

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, roomId, adoptCritter.getCritterId(), LogTargetType.CRITTER, ActionType.ADOPT_CRITTER));

        return adoptCritter;
    }

    public void processInteraction(Long userId, Long roomId, Long critterId, double mouseX, double mouseY, List<CritterLocationDto> critters) {
        critters.stream()
            .filter(c -> c.getCritterId().equals(critterId))
            .findFirst()
            .ifPresent(critter -> {
                // 패닉 로직
                // 즉각 적용+반응
                critter.setStatus("PANIC");
                double dx = critter.getX() - mouseX;
                double dy = critter.getY() - mouseY;
                double dist = Math.sqrt(dx*dx + dy*dy);
                critter.setVx((dx / dist) * 5.0);
                critter.setVy((dy / dist) * 5.0);

                // 로그 기록
                eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, roomId, critterId, LogTargetType.CRITTER, ActionType.INTERACT_PANIC));
            });
    }
}