package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.event.ActionLogEvents;
import com.critter.critter_backend.event.PointEvents;
import com.critter.critter_backend.exception.ResourceNotFoundException;
import com.critter.critter_backend.exception.account.ForbiddenException;
import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.BoardCategory;
import com.critter.critter_backend.domain.PointReason;
import com.critter.critter_backend.domain.LogTargetType;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.repository.BoardRepository;
import com.critter.critter_backend.repository.CommentRepository;
import com.critter.critter_backend.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;

    
    // 게시글 등록
    @Transactional
    public Board createBoard(Long writerId, BoardCategory category, String title, String content) {
        Account writer = accountRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 유저입니다."));

        Board board = new Board();
        board.setWriter(writer);
        board.setTitle(title);
        board.setCategory(category);
        board.setContent(content);
        Board savedBoard = boardRepository.save(board);

        eventPublisher.publishEvent(new PointEvents.Earn(writerId, 5L, PointReason.POST_BOARD));

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(writerId, null, savedBoard.getBoardId(), LogTargetType.BOARD, ActionType.POST_BOARD));

        return savedBoard;
    }


    // 전체 게시글 조회
    public List<Board> getAllBoards(BoardCategory category) {
        if (category == null || category == BoardCategory.ALL) {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }
        return boardRepository.findByCategoryOrderByCreatedAtDesc(category);
    }


    // 게시글 상세 조회
    public Board getBoardDetail(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 글입니다."));
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 글입니다."));

        Account writer = accountRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 유저입니다."));

        Comment comment = Comment.builder()
                .board(board)
                .writer(writer)
                .content(content)
                .build();
        Comment savedComment = commentRepository.save(comment);

        eventPublisher.publishEvent(new PointEvents.Earn(writerId, 3L, PointReason.POST_COMMENT));

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(writerId, null, savedComment.getCommentId(), LogTargetType.COMMENT, ActionType.POST_COMMENT));

        return savedComment;
    }


    // 댓글 목록 조회
    public List<Comment> getCommentsByBoard(Long boardId) {
        // 먼저 게시글이 존재하는지 검증
        if (!boardRepository.existsById(boardId)) {
            throw new ResourceNotFoundException("존재하지 않는 글입니다.");
        }
        return commentRepository.findByBoard_BoardIdOrderByCreatedAtAsc(boardId);
    }


    // 게시글 수정
    @Transactional
    public Board updateBoard(Long userId, Long boardId, String title, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 글입니다."));

        if (!board.getWriter().getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 글만 수정할 수 있습니다.");
        }
        
        board.setTitle(title);
        board.setContent(content);
        Board savedBoard = boardRepository.save(board);

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(savedBoard.getWriter().getUserId(), null, boardId, LogTargetType.BOARD, ActionType.UPDATE_BOARD));

        return savedBoard;
    }


    // 댓글 수정
    @Transactional
    public Comment updateComment(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getWriter().getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 댓글만 수정할 수 있습니다.");
        }
        
        comment.setContent(content);
        Comment savedComment = commentRepository.save(comment);

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(savedComment.getWriter().getUserId(), null, commentId, LogTargetType.COMMENT, ActionType.UPDATE_COMMENT));

        return savedComment;
    }


    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 글입니다."));
    
        // 본인 확인 로직
        if (!board.getWriter().getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 글만 삭제할 수 있습니다.");
        }

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, null, boardId, LogTargetType.BOARD, ActionType.DELETE_BOARD));

        boardRepository.deleteById(boardId);
    }


    // 댓글 삭제
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 댓글입니다."));

        // 본인 확인 로직
        if (!comment.getWriter().getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 댓글만 삭제할 수 있습니다.");
        }

        eventPublisher.publishEvent(new ActionLogEvents.recordActionLog(userId, null, commentId, LogTargetType.COMMENT, ActionType.DELETE_COMMENT));

        commentRepository.deleteById(commentId);
    }
}