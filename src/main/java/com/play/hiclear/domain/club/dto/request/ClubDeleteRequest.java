package com.play.hiclear.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClubDeleteRequest {

    @NotBlank
    private String password;
}
