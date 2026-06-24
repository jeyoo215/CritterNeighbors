package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Ecosystem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EcosystemRepository extends JpaRepository<Ecosystem, Long> {
    // "내가 가진 룸 목록 대시보드" 조회를 위해 유저 ID로 룸들을 찾는 메서드
    List<Ecosystem> findByAccount_UserId(Long userId);
}