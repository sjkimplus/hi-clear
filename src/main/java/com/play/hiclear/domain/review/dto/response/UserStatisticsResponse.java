package com.play.hiclear.domain.review.dto.response;

import lombok.Getter;

@Getter
public class UserStatisticsResponse {
    private String averageMannerScore;
    private String averageGradeRank;

    public UserStatisticsResponse(String averageMannerScore, String averageGradeRank) {
        this.averageMannerScore = averageMannerScore;
        this.averageGradeRank = averageGradeRank;
    }
}
