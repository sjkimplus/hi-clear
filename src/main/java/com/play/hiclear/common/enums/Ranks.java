package com.play.hiclear.common.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Ranks {
    RANK_A(1),
    RANK_B(2),
    RANK_C(3),
    RANK_D(4),
    RANK_E(5),
    RANK_F(6);

    private final int rankValue;

    public static Ranks of(String rank) {
        return Arrays.stream(Ranks.values())
                .filter(r -> r.name().equalsIgnoreCase(rank))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등급입니다 \'RANK_O\'양식으로 O자리에 A~F사이 값을 입력해주세요"));
    }

    // Constructor to set the corresponding number
    Ranks(int rankValue) {
        this.rankValue = rankValue;
    }

    public static Ranks fromRankValue(int rankValue) {
        for(Ranks rank : values()) {
            if(rank.rankValue == rankValue) {
                return rank;
            }
        }
        throw new CustomException(ErrorCode.REVIEW_MISS_SCORE);
    }

    // Getter method to access the corresponding number
    public int getRankValue() {
        return rankValue;
    }
}
