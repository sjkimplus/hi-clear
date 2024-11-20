package com.play.hiclear.domain.likes.service;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.likes.entity.Likes;
import com.play.hiclear.domain.likes.repository.LikesRepository;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.service.NotiService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotiService notiService;

    /**
     * 좋아요 토글기능
     *
     * @param commentId
     * @param authUser
     */
    @Transactional
    public void toggleLike(Long commentId, AuthUser authUser) {

        // Comment 조회
        Comment comment = commentRepository.findByIdAndDeletedAtIsNullOrThrow(commentId);

        Long userId = authUser.getUserId();

        // 사용자 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        // 기존 좋아요 상태 확인
        Likes existingLike = likesRepository.findByCommentIdAndUserId(commentId, user.getId());

        if (existingLike != null) {// 이미 좋아요가 있는 경우 상태 토글
            existingLike.toggleLike();

            if (existingLike.isStatus()) {
                notiService.sendNotification(
                        comment.getUser(),
                        NotiType.COMMENT,
                        String.format("%s님이 %s 댓글에 좋아요를 눌렀습니다", user.getName(), comment.getContent()),
                        String.format("/v1/comments/%d/likes", comment.getId())
                );
            }
        } else {// 좋아요가 없는 경우 새 좋아요 생성
            Likes likes = new Likes(
                    user,
                    comment
            );
            likesRepository.save(likes);

            notiService.sendNotification(
                    comment.getUser(),
                    NotiType.COMMENT,
                    String.format("%s님이 %s 댓글에 좋아요를 눌렀습니다", user.getName(), comment.getContent()),
                    String.format("/v1/comments/%d/likes", comment.getId())
            );

        }
    }

    /**
     * 좋아요 총개수 카운트
     *
     * @param commentId
     * @return
     */
    public Long get(Long commentId) {

        // Comment 조회
        commentRepository.findByIdAndDeletedAtIsNullOrThrow(commentId);

        return likesRepository.countByCommentId(commentId);
    }

}
