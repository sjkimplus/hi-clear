package com.play.hiclear.domain.likes.repository;

import com.play.hiclear.domain.likes.entity.Likes;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends CrudRepository<Likes, Long> {
    Optional<Likes> findByCommentIdAndUserId(Long commentId, Long userId);
    List<Likes> findByCommentId(Long commentId);
}
