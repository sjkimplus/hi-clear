package com.play.hiclear.domain.clubmember.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClubMemberExpelRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
