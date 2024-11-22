package com.play.hiclear.domain.comment.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);

    default Comment findByIdAndDeletedAtIsNullOrThrow(Long commentId){
        return findByIdAndDeletedAtIsNull(commentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Comment.class.getSimpleName()));
    }
}
