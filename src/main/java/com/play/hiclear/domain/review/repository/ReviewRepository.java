package com.play.hiclear.domain.review.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.review.entity.Review;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewee(User reviewee);

    boolean existsByMeetingAndRevieweeAndReviewer(Meeting meeting, User reviewee, User reviewer);

    default Review findByReviewIdOrThrow(Long reviewId) {
        return findById(reviewId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, Review.class.getName()));
    }
}
