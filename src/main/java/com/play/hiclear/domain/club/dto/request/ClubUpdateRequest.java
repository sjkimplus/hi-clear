package com.play.hiclear.domain.club.dto.request;

import lombok.Getter;

@Getter
public class ClubUpdateRequest {

    private String clubname;
    private Integer clubSize;
    private String intro;
    private String region;
    private String password;
}