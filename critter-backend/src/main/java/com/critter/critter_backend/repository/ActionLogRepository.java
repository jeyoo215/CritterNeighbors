package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.ActionLog;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    boolean existsByAccountIdAndActionTypeAndCreatedAtAfter(Long userId, String actionType, LocalDateTime time);
}