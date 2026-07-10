package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Critter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CritterRepository extends JpaRepository<Critter, Long> {
    // 특정 방에 입장했을 때 그 방의 동물들을 모집 + 메모리 저장
    List<Critter> findByEcosystem_RoomId(Long roomId);
}