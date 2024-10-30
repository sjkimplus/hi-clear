package com.play.hiclear.domain.likes.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.likes.entity.Likes;
import com.play.hiclear.domain.likes.repository.LikesRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void toggleLike(Long commentId, AuthUser authUser) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND, "해당 댓글"));

        Long userId = authUser.getUserId();

        // 사용자 확인
        User user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "해당 사용자"));


        // 기존 좋아요 상태 확인
        Likes existingLike = likesRepository.findByCommentIdAndUserId(commentId, user.getId())
                .orElse(null);

        if (existingLike != null) {// 이미 좋아요가 있는 경우 상태 토글
            existingLike.toggleLike();
            likesRepository.save(existingLike);
        } else {// 좋아요가 없는 경우 새 좋아요 생성
            Likes likes = new Likes(
                    user,
                    comment
            );
            likesRepository.save(likes);
        }
    }

    public Long get(Long commentId) {
        return likesRepository.countByCommentId(commentId);
    }
}
