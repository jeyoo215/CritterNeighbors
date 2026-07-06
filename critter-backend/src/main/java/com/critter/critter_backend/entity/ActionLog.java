package com.critter.critter_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.LogTargetType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACTIONLOG")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTION_ID")
    private Long actionId;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;
    
    @Column(name = "ROOM_ID", nullable = true)
    private Long roomId;

    @Column(name = "TARGET_ID", nullable = true)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_TYPE", nullable = true)
    private LogTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION_TYPE", nullable = false)
    private ActionType actionType;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}