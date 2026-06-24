package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
    // 특정 방에 남겨진 방명록들을 최신순으로 긁어오기 위한 메서드
    List<Guestbook> findByEcosystem_RoomIdOrderByCreatedAtDesc(Long roomId);
}