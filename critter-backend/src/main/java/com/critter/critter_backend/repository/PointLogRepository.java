package com.critter.critter_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.entity.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    // 유저별 포인트 로그를 최신순으로 가져오기 등 필요할 때 씀
    List<PointLog> findByAccountIdOrderByCreatedAtDesc(Long userId);
    
    // 데일리 미션
    boolean existsByAccountIdAndReasonAndCreatedAtAfter(Long userId, PointReason reason, LocalDateTime time);
}