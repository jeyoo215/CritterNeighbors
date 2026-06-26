package com.critter.critter_backend.entity;

import com.critter.critter_backend.domain.CritterStatus;
import com.critter.critter_backend.domain.CritterType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "CRITTERS")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Critter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CRITTER_ID")
    private Long critterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID", nullable = false)
    private Ecosystem ecosystem;

    @Column(name = "CRITTER_NAME", nullable = false)
    private String critterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "CRITTER_TYPE", nullable = false)
    private CritterType critterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private CritterStatus status;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}