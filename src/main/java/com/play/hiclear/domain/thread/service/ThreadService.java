package com.play.hiclear.domain.thread.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.thread.dto.request.ThreadCreateRequest;
import com.play.hiclear.domain.thread.dto.request.ThreadDeleteRequest;
import com.play.hiclear.domain.thread.dto.request.ThreadUpdateRequest;
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
    public void create(Long userId, Long commentId, ThreadCreateRequest threadCreateRequest) {

        User user = findUserById(userId);
        Comment comment = findCommentById(commentId);

        Thread thread = Thread.builder()
                .content(threadCreateRequest.getContent())
                .user(user)
                .comment(comment)
                .build();

        threadRepository.save(thread);
    }

    @Transactional
    public void update(Long userId, Long threadId, ThreadUpdateRequest threadUpdateRequest) {

        User user = findUserById(userId);
        Thread thread = findThreadById(threadId);

        checkThreadUser(user, thread);
        checkPassword(threadUpdateRequest.getPassword(), user.getPassword());

        thread.update(threadUpdateRequest);
    }

    @Transactional
    public void delete(Long userId, Long threadId, ThreadDeleteRequest threadDeleteRequest) {

        User user = findUserById(userId);
        Thread thread = findThreadById(threadId);

        checkThreadUser(user, thread);
        checkPassword(threadDeleteRequest.getPassword(), user.getPassword());

        thread.markDeleted();
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
    }

    // Comment 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNullOrThrow(commentId);

    }

    // Thread 조회
    private Thread findThreadById(Long threadId) {
        return threadRepository.findByIdAndDeletedAtIsNullOrThrow(threadId);
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
            throw new CustomException(ErrorCode.NOT_FOUND, Thread.class.getSimpleName());
        }
    }
}
