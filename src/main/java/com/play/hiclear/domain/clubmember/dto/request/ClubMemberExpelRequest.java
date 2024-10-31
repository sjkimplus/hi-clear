package com.play.hiclear.domain.clubmember.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubMemberExpelRequest {

    @NotNull @NotBlank
    private String email;
    @NotNull @NotBlank
    private String password;
}
