package com.play.hiclear.domain.club.dto.response;

import lombok.Getter;

@Getter
public class ClubNearResponse {

    private final String clubname;
    private final String region;
    private final Double distance;

    public ClubNearResponse(String clubname, String region, Double distance) {
        this.clubname = clubname;
        this.region = region;
        this.distance = distance;
    }


}
