package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 보드에 달린 코멘트만 순서대로 겟
    List<Comment> findByBoard_BoardIdOrderByCreatedAtAsc(Long boardId);
}