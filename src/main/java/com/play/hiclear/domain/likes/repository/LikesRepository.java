package com.play.hiclear.domain.likes.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.likes.entity.Likes;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikesRepository extends CrudRepository<Likes, Long> {
    Likes findByCommentIdAndUserId(Long commentId, Long userId);

    Long countByCommentId(Long commentId);

    Optional<Likes> findById(Long LikesId);

    default Likes findByLikesIdOrThrow(Long LikesId){
        return findById(LikesId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, Likes.class.getSimpleName()));
    }
}
