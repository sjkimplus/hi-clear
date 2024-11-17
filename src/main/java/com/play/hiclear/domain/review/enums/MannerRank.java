package com.play.hiclear.domain.review.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum MannerRank {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int mannerScore;

    MannerRank(int mannerScore) {
        this.mannerScore = mannerScore;
    }

    public static MannerRank fromScore(int score) {
        for(MannerRank rank : values()) {
            if(rank.mannerScore == score) {
                return rank;
            }
        }
        throw new CustomException(ErrorCode.REVIEW_MISS_SCORE);
    }

}
