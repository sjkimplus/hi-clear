package com.play.hiclear.domain.review.dto.request;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.review.enums.MannerRank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewCreateRequest {
    private Long revieweeId;
    private MannerRank mannerRank;
    private Ranks gradeRank;

    // 기본 생성자
    public ReviewCreateRequest() {}

    // 생성자
    public ReviewCreateRequest(Long revieweeId, MannerRank mannerRank, Ranks gradeRank) {
        this.revieweeId = revieweeId;
        this.mannerRank = mannerRank;
        this.gradeRank = gradeRank;
    }
}
