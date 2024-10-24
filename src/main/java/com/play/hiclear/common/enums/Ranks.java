package com.play.hiclear.common.enums;

import lombok.Getter;

@Getter
public enum Ranks {
    RANK_A(1),
    RANK_B(2),
    RANK_C(3),
    RANK_D(4),
    RANK_E(5),
    RANK_F(6);

    private final int rankValue;

    // Constructor to set the corresponding number
    Ranks(int rankValue) {
        this.rankValue = rankValue;
    }

    // Getter method to access the corresponding number
    public int getRankValue() {
        return rankValue;
    }

    // 급수를 숫자로 가져온다
    public static int convertRankToNumber(Ranks ranks) {
        return ranks.getRankValue();
    }
}
