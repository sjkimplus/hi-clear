package com.play.hiclear.domain.review.enums;

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

    public int getScore(){
        return mannerScore;
    }
}
