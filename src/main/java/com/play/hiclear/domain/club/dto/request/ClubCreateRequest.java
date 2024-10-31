package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubCreateRequest {

    @NotNull @NotBlank
    private String clubname;
    @NotNull @NotBlank
    private Integer clubSize;
    private String intro;
    @NotNull @NotBlank
    private String region;
    @NotNull @NotBlank
    private String password;

    public ClubCreateRequest(String clubname, Integer clubSize, String intro, String region, String password) {
        this.clubname = clubname;
        this.clubSize = clubSize;
        this.intro = intro;
        this.region = region;
        this.password = password;
    }
}
