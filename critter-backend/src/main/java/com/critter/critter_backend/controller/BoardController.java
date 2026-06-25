package com.critter.critter_backend.controller;

import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards") // RESTful 표준 규격 주소 설계
@CrossOrigin(origins = "http://localhost:5173") // 리액트 연동 완벽 대비
public class BoardController {

    private final BoardService boardService;

    /*
     * 1. 질문 게시글 등록 API
     * POST /api/boards
    */
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Map<String, Object> requestBody) {
        Long writerId = Long.valueOf(requestBody.get("writerId").toString());
        String title = requestBody.get("title").toString();
        String content = requestBody.get("content").toString();

        Board savedBoard = boardService.createBoard(writerId, title, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    /*
     *  2. 전체 질문 게시글 목록 조회 API (최신순)
     * GET /api/boards
    */
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }

    /*
     * 3. 특정 질문 게시글 상세 조회 API
     * GET /api/boards/{boardId}
    */
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoardDetail(@PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(board);
    }

    /*
     * 4. 특정 질문글에 댓글 등록 API
     * POST /api/boards/{boardId}/comments
    */
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody Map<String, Object> requestBody) {
        
        Long writerId = Long.valueOf(requestBody.get("writerId").toString());
        String content = requestBody.get("content").toString();

        Comment savedComment = boardService.createComment(boardId, writerId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    /*
     * 5. 특정 질문글에 달린 댓글 목록 조회 API (등록순)
     * GET /api/boards/{boardId}/comments
    */
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByBoard(@PathVariable("boardId") Long boardId) {
        List<Comment> comments = boardService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(comments);
    }
}