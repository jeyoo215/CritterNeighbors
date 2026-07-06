package com.critter.critter_backend.repository;

import com.critter.critter_backend.domain.BoardCategory;
import com.critter.critter_backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 전체
    List<Board> findAllByOrderByCreatedAtDesc();

    // 카테고리별
    List<Board> findByCategoryOrderByCreatedAtDesc(BoardCategory category);

    // 다음 글
    Board findFirstByBoardIdGreaterThanOrderByBoardIdAsc(Long boardId);

    // 이전 글
    Board findFirstByBoardIdLessThanOrderByBoardIdDesc(Long boardId);
}