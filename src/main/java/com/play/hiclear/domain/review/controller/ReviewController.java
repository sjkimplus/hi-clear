package com.play.hiclear.domain.review.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.review.dto.request.ReviewCreateRequest;
import com.play.hiclear.domain.review.dto.response.ReviewCreateResponse;
import com.play.hiclear.domain.review.dto.response.ReviewSearchResponse;
import com.play.hiclear.domain.review.dto.response.UserStatisticsResponse;
import com.play.hiclear.domain.review.service.ReviewDummyService;
import com.play.hiclear.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewDummyService reviewDummyService;

    // 리뷰 가능한 유저 가져오기
    @GetMapping("/v1/reviewable")
    public ResponseEntity<List<ReviewSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser
    ){
        return ResponseEntity.ok(reviewService.search(authUser));
    }

    // 리뷰 생성
    @PostMapping("/v1/meetings/{meetingId}/reviews")
    public ResponseEntity<ReviewCreateResponse> create(
            @PathVariable Long meetingId,
            @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ){
        return ResponseEntity.ok(reviewService.create(meetingId, request, authUser));
    }

    // 점수 조회
    @GetMapping("/v1/statistics/{userId}")
    public ResponseEntity<UserStatisticsResponse> statistics(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(reviewService.statistics(userId));
    }

    // 리뷰 10000개 추가 API
    @PostMapping("/v1/reviews/dummy")
    public ResponseEntity<String> dummy(){
        reviewDummyService.generateDummyReviews();
        return ResponseEntity.ok("더미데이터 생성 완료!");
    }
}
