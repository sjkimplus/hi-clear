package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubUpdateRequest {

    @NotBlank
    private String clubname;
    @NotNull
    private Integer clubSize;
    @NotBlank
    private String intro;
    @NotBlank
    private String region;
    @NotBlank
    private String password;
}
