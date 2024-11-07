package com.play.hiclear.domain.review.dto.request;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.review.enums.MannerRank;
import lombok.Getter;

@Getter
public class ReviewCreateRequest {
    private Long revieweeId;
    private MannerRank mannerRank;
    private Ranks gradeRank;
}
