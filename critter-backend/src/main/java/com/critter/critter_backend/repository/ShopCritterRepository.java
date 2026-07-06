package com.critter.critter_backend.repository;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.entity.ShopCritter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopCritterRepository extends JpaRepository<ShopCritter, Long> {
    Optional<ShopCritter> findByType(CritterType type);
}