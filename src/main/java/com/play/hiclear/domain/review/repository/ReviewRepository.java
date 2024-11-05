package com.play.hiclear.domain.review.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.review.entity.Review;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByReviewee(User reviewee);

    Optional<Review> findById(Long reviewId);

    default Review findByReviewIdOrThrow(Long reviewId) {
        return findById(reviewId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, Review.class.getName()));
    }
}
