package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
    // 특정 방에 입장했을 때 그 방의 동물들을 긁어와서 메모리에 올려야
    List<Creature> findByEcosystem_RoomId(Long roomId);
}