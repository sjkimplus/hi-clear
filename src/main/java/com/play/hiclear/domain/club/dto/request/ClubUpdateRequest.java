package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClubUpdateRequest {

    @NotBlank
    private String clubname;
    @NotBlank
    private Integer clubSize;
    @NotBlank
    private String intro;
    @NotBlank
    private String region;
    @NotBlank
    private String password;
}
