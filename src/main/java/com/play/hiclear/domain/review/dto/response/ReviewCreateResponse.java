package com.play.hiclear.domain.review.dto.response;

import lombok.Getter;

@Getter
public class ReviewCreateResponse {
    private Long id;
    private Long reviewerId;
    private Long revieweeId;
    private String mannerRank;
    private String gradeRank;

    public ReviewCreateResponse(Long id, Long reviewerId, Long revieweeId, String mannerRank, String gradeRank) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.mannerRank = mannerRank;
        this.gradeRank = gradeRank;
    }
}
