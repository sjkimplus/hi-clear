package com.play.hiclear.domain.review.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewSearchResponse {
    private String userName;
    private String meetingTitle;
    private String regionAddress;
    private LocalDateTime endDate;

    public ReviewSearchResponse(String userName, String meetingTitle, String region, LocalDateTime endDate) {
        this.userName = userName;
        this.meetingTitle = meetingTitle;
        this.regionAddress = region;
        this.endDate = endDate;
    }
}
