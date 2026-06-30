package com.critter.critter_backend.repository;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.entity.CritterTemplate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CritterTemplateRepository extends JpaRepository<CritterTemplate, Long> {
    Optional<CritterTemplate> findByType(CritterType type);
}