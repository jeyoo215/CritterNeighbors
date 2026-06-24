package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    // 대용량 로그 적재용 상자
}