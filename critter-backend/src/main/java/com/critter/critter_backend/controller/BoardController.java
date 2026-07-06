package com.critter.critter_backend.controller;

import com.critter.critter_backend.dto.BoardRequestDto;
import com.critter.critter_backend.dto.CommentRequestDto;
import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.service.BoardService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
@CrossOrigin(origins = "http://localhost:5173") // 리액트 연동 완벽 대비
public class BoardController {

    private final BoardService boardService;

    /*
        게시글 등록
        POST /api/boards
    */
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequestDto dto,
        HttpSession session) {
        Long writerId = (Long) session.getAttribute("USER_ID");
        if (writerId == null) return ResponseEntity.status(401).build();

        Board savedBoard = boardService.createBoard(writerId, dto.getTitle(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    /*
        게시글 목록 조회
        GET /api/boards
    */
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }

    /*
        게시글 상세 조회
        GET /api/boards/{boardId}
    */
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoardDetail(@PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(board);
    }

    /*
        게시글 이전 글 다음 글 조회
        GET /api/boards/{boardId}/neighbors
    */
    @GetMapping("/{boardId}/neighbors")
    public ResponseEntity<Map<String, Board>> getNeighbors(@PathVariable Long boardId) {
        Map<String, Board> neighbors = new HashMap<>();
    
        neighbors.put("next", boardService.getNextBoard(boardId));
        neighbors.put("prev", boardService.getPrevBoard(boardId));
    
        return ResponseEntity.ok(neighbors);
    }

    /*
        댓글 등록
        POST /api/boards/{boardId}/comments
    */
    @PostMapping("/{boardId}/comments")
        public ResponseEntity<Comment> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequestDto dto,
            HttpSession session) {

            Long writerId = (Long) session.getAttribute("USER_ID");
            if (writerId == null) return ResponseEntity.status(401).build();

            Comment savedComment = boardService.createComment(boardId, writerId, dto.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        }

    /*
        댓글 목록 조회
        GET /api/boards/{boardId}/comments
    */
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByBoard(@PathVariable("boardId") Long boardId) {
        List<Comment> comments = boardService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(comments);
    }

    /*
        게시글 수정
        PUT /api/boards/{boardId}
    */
    @PutMapping("/{boardId}")
    public ResponseEntity<Board> updateBoard(@PathVariable("boardId") Long boardId,
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        String title = (String) body.get("title");
        String content = (String) body.get("content");
        return ResponseEntity.ok(boardService.updateBoard(userId, boardId, title, content));
    }

    /*
        댓글 수정
        PUT /api/boards/comments/{commentId}
    */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable("commentId") Long commentId, 
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();
        
        String content = (String) body.get("content");
        return ResponseEntity.ok(boardService.updateComment(userId, commentId, content));
    }

    /*
        게시글 삭제
        DELETE /api/boards/{boardId}
    */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("boardId") Long boardId,
        HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        boardService.deleteBoard(userId, boardId);
        return ResponseEntity.noContent().build();
    }

    /*
        댓글 삭제
        DELETE /api/boards/comments/{commentId}
    */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
        HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        boardService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}