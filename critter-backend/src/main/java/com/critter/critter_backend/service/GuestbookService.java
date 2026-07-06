package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.Guestbook;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.entity.Ecosystem; // 네 프로젝트의 방 엔티티 클래스명 확인!
import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.LogTargetType;
import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.exception.SelfGuestbookForbiddenException;
import com.critter.critter_backend.repository.GuestbookRepository;
import com.critter.critter_backend.repository.EcosystemRepository; // 방 레포지토리
import com.critter.critter_backend.repository.AccountRepository; // 유저 레포지토리

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final EcosystemRepository ecosystemRepository;
    private final AccountRepository accountRepository;
    
    private final ApplicationEventPublisher eventPublisher;

    // 방명록 비동기 등록
    @Transactional
    public Guestbook createGuestbook(Long roomId, Long writerId, String content) {
        // 1. 방 존재 여부 및 방 주인 ID 조회
        Ecosystem room = ecosystemRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 생태계 방입니다."));
        
        // 2. 작성자 유저 조회
        Account writer = accountRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 3. 자가 방명록 작성 차단!
        // 방 주인의 ID와 방명록 작성자의 ID가 같다면 예외 폭탄 투하
        if (room.getAccount().getUserId().equals(writerId)) {
            throw new SelfGuestbookForbiddenException("본인의 생태계 방에는 방명록을 남길 수 없습니다.");
        }

        // 4. 빌더나 생성자로 Guestbook 객체 생성 후 저장 (네 엔티티 구조에 맞게 세팅)
        Guestbook guestbook = new Guestbook();
        guestbook.setEcosystem(room); // 네 레포지토리 쿼리 메서드 기반 명칭 매핑
        guestbook.setWriter(writer);
        guestbook.setContent(content);
        Guestbook savedGuestbook = guestbookRepository.save(guestbook);

        eventPublisher.publishEvent(new PointEvents.Earn(writerId, 5L, PointReason.POST_GUESTBOOK));

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(writerId, roomId, savedGuestbook.getGuestbookId(), LogTargetType.GUESTBOOK, ActionType.POST_GUESTBOOK));
        
        return savedGuestbook;
    }

    // 특정 방의 방명록 목록 조회 (네가 만든 최신순 쿼리 메서드 호출)
    public List<Guestbook> getGuestbooksByRoom(Long roomId) {
        return guestbookRepository.findByEcosystem_RoomIdOrderByCreatedAtDesc(roomId);
    }
}