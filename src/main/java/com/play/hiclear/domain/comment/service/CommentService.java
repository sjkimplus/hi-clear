package com.play.hiclear.domain.comment.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.board.repository.BoardRepository;
import com.play.hiclear.domain.comment.dto.CommentCreateRequest;
import com.play.hiclear.domain.comment.dto.CommentDeleteRequest;
import com.play.hiclear.domain.comment.dto.CommentUpdateRequest;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(Long userId, Long clubboardsId, CommentCreateRequest commentCreateRequest) {

        Board board = findBoardById(clubboardsId);
        User user = findUserById(userId);

        Comment comment = Comment.builder()
                .content(commentCreateRequest.getContent())
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void update(Long userId, Long commentId, CommentUpdateRequest commentUpdateRequest) {

        User user = findUserById(userId);
        Comment comment = findCommentById(commentId);

        checkCommentUser(user, comment);
        checkPassword(commentUpdateRequest.getPassword(), user.getPassword());

        comment.updateComment(commentUpdateRequest);
    }

    @Transactional
    public void delete(Long userId, Long commentId, CommentDeleteRequest commentDeleteRequest) {

        User user = findUserById(userId);
        Comment comment = findCommentById(commentId);

        checkCommentUser(user, comment);
        checkPassword(commentDeleteRequest.getPassword(), user.getPassword());

        comment.markDeleted();
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저를"));
    }

    // Board 조회
    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 게시글을"));
    }

    // Comment 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 댓글을"));
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // 작성자 확인
    private void checkCommentUser(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
    }
}
