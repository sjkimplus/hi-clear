package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubCreateRequest {

    @NotBlank
    private String clubname;
    @NotNull
    private Integer clubSize;
    private String intro;
    @NotBlank
    private String address;
    @NotBlank
    private String password;

    public ClubCreateRequest(String clubname, Integer clubSize, String intro, String address, String password) {
        this.clubname = clubname;
        this.clubSize = clubSize;
        this.intro = intro;
        this.address = address;
        this.password = password;
    }
}
