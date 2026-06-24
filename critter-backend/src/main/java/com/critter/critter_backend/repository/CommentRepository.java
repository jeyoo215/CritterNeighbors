package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 질문글(Board)에 달린 댓글들만 순서대로 가져오는 쿼리
    List<Comment> findByBoard_BoardIdOrderByCreatedAtAsc(Long boardId);
}