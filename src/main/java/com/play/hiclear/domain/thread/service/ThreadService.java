package com.play.hiclear.domain.thread.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.thread.dto.ThreadCreateRequest;
import com.play.hiclear.domain.thread.dto.ThreadDeleteRequest;
import com.play.hiclear.domain.thread.dto.ThreadUpdateRequest;
import com.play.hiclear.domain.thread.entity.Thread;
import com.play.hiclear.domain.thread.repository.ThreadRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThreadService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(Long userId, Long commentsId, ThreadCreateRequest threadCreateRequest) {

        User user = findUserById(userId);
        Comment comment = findCommentById(commentsId);

        Thread thread = Thread.builder()
                .content(threadCreateRequest.getContent())
                .user(user)
                .comment(comment)
                .build();

        threadRepository.save(thread);
    }

    @Transactional
    public void update(Long userId, Long threadsId, ThreadUpdateRequest threadUpdateRequest) {

        User user = findUserById(userId);
        Thread thread = findThreadById(threadsId);

        checkThreadUser(user, thread);
        checkPassword(threadUpdateRequest.getPassword(), user.getPassword());

        thread.updateThread(threadUpdateRequest);
    }

    @Transactional
    public void delete(Long userId, Long threadsId, ThreadDeleteRequest threadDeleteRequest) {

        User user = findUserById(userId);
        Thread thread = findThreadById(threadsId);

        checkThreadUser(user, thread);
        checkPassword(threadDeleteRequest.getPassword(), user.getPassword());

        thread.markDeleted();
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저를"));
    }

    // Comment 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 댓글을"));
    }

    // Thread 조회
    private Thread findThreadById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 대댓글을"));
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // 작성자 확인
    private void checkThreadUser(User user, Thread thread) {
        if (!thread.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
    }
}
