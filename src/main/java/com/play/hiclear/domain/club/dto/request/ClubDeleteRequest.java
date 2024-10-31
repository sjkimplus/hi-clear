package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClubDeleteRequest {

    @NotBlank
    private String password;
}
