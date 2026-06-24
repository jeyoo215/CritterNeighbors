package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 질문글 테이블 전체를 최신 등록일 순으로 정렬해서 가져오는 쿼리
    List<Board> findAllByOrderByCreatedAtDesc();
}