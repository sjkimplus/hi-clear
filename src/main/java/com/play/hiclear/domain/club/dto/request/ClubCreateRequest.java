package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClubCreateRequest {

    @NotBlank
    private String clubname;
    @NotBlank
    private Integer clubSize;
    private String intro;
    @NotBlank
    private String region;
    @NotBlank
    private String password;

    public ClubCreateRequest(String clubname, Integer clubSize, String intro, String region, String password) {
        this.clubname = clubname;
        this.clubSize = clubSize;
        this.intro = intro;
        this.region = region;
        this.password = password;
    }
}
