package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.repository.BoardRepository;
import com.critter.critter_backend.repository.CommentRepository;
import com.critter.critter_backend.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;

    
    // 게시글 등록
    @Transactional
    public Board createBoard(Long writerId, String title, String content) {
        Account writer = accountRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Board board = new Board();
        board.setWriter(writer);
        board.setTitle(title);
        board.setContent(content);

        return boardRepository.save(board);
    }


    // 전체 게시글 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }


    // 게시글 상세 조회
    public Board getBoardDetail(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문글입니다."));
    }

    // BoardService.java 에 추가

    public Board getNextBoard(Long currentBoardId) {
        return boardRepository.findFirstByBoardIdGreaterThanOrderByBoardIdAsc(currentBoardId);
    }

    public Board getPrevBoard(Long currentBoardId) {
        return boardRepository.findFirstByBoardIdLessThanOrderByBoardIdDesc(currentBoardId);
    }


    // 댓글 등록 
    @Transactional
    public Comment createComment(Long boardId, Long writerId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문글입니다."));

        Account writer = accountRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 빌더 패턴으로 생성
        Comment comment = Comment.builder()
                .board(board)
                .writer(writer)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }


    // 댓글 목록 조회
    public List<Comment> getCommentsByBoard(Long boardId) {
        // 먼저 게시글이 존재하는지 검증
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException("존재하지 않는 질문글입니다.");
        }
        return commentRepository.findByBoard_BoardIdOrderByCreatedAtAsc(boardId);
    }


    // 게시글 수정
    @Transactional
    public Board updateBoard(Long boardId, String title, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        board.setTitle(title);
        board.setContent(content);
        return boardRepository.save(board);
    }


    // 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        comment.setContent(content);
        return commentRepository.save(comment);
    }


    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }


    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}