package com.play.hiclear.domain.review.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.review.dto.request.ReviewCreateRequest;
import com.play.hiclear.domain.review.dto.response.ReviewCreateResponse;
import com.play.hiclear.domain.review.dto.response.ReviewSearchResponse;
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
}
