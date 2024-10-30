package com.play.hiclear.domain.club.dto.request;

import lombok.Getter;

@Getter
public class ClubCreateRequest {

    private String clubname;
    private Integer clubSize;
    private String intro;
    private String region;
    private String password;

    public ClubCreateRequest(String clubname, Integer clubSize, String intro, String region, String password) {
        this.clubname = clubname;
        this.clubSize = clubSize;
        this.intro = intro;
        this.region = region;
        this.password = password;
    }
}
