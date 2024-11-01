package com.play.hiclear.domain.comment.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.board.repository.BoardRepository;
import com.play.hiclear.domain.comment.dto.request.CommentCreateRequest;
import com.play.hiclear.domain.comment.dto.request.CommentDeleteRequest;
import com.play.hiclear.domain.comment.dto.request.CommentUpdateRequest;
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

    /**
     *
     * @param userId    로그인된 userId
     * @param clubboardId   댓글을 작성할 boardId
     * @param commentCreateRequest  댓글 내용을 담은 DTO
     */
    @Transactional
    public void create(Long userId, Long clubboardId, CommentCreateRequest commentCreateRequest) {

        //  유저 조회
        User user = findUserById(userId);
        //  게시글 조회
        Board board = findBoardById(clubboardId);

        Comment comment = Comment.builder()
                .content(commentCreateRequest.getContent())
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);
    }

    /**
     *
     * @param userId    로그인된 userId
     * @param commentId 수정할 commentId
     * @param commentUpdateRequest  수정할 댓글 내용과 비밀번호을 담은 DTO
     */
    @Transactional
    public void update(Long userId, Long commentId, CommentUpdateRequest commentUpdateRequest) {

        //  유저 조회
        User user = findUserById(userId);
        //  댓글 조회
        Comment comment = findCommentById(commentId);

        //  작성자 확인
        checkCommentUser(user, comment);
        //  비밀번호 확인
        checkPassword(commentUpdateRequest.getPassword(), user.getPassword());

        comment.updateComment(commentUpdateRequest);
    }

    /**
     *
     * @param userId    로그인된 userId
     * @param commentId 삭제할 commentId
     * @param commentDeleteRequest  비밀번호를 담은 DTO
     */
    @Transactional
    public void delete(Long userId, Long commentId, CommentDeleteRequest commentDeleteRequest) {

        //  유저 조회
        User user = findUserById(userId);
        //  댓글 조회
        Comment comment = findCommentById(commentId);

        //  작성자 확인
        checkCommentUser(user, comment);
        //  비밀번호 확인
        checkPassword(commentDeleteRequest.getPassword(), user.getPassword());

        comment.markDeleted();
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
    }

    // Board 조회
    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));
    }

    // Comment 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNullOrThrow(commentId);
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
            throw new CustomException(ErrorCode.NOT_FOUND, Comment.class.getSimpleName());
        }
    }
}
