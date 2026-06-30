package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Ecosystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EcosystemRepository extends JpaRepository<Ecosystem, Long> {
    // 내 방
    List<Ecosystem> findByAccount_UserId(Long userId);

    // 남의 방
    @Query(value = "SELECT * FROM ecosystems WHERE user_id != :userId ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Ecosystem> findRandomRoomsExcludingUser(@Param("userId") Long userId);
}