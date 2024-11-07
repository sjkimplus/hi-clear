package com.play.hiclear.domain.review.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewSearchResponse {
    private String userName;
    private String meetingTitle;
    private String region;
    private LocalDateTime endDate;

    public ReviewSearchResponse(String userName, String meetingTitle, String region, LocalDateTime endDate) {
        this.userName = userName;
        this.meetingTitle = meetingTitle;
        this.region = region;
        this.endDate = endDate;
    }
}
