package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubUpdateRequest {

    @NotNull @NotBlank
    private String clubname;
    @NotNull @NotBlank
    private Integer clubSize;
    @NotNull @NotBlank
    private String intro;
    @NotNull @NotBlank
    private String region;
    @NotNull @NotBlank
    private String password;
}
