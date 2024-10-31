package com.play.hiclear.domain.clubmember.dto;

import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClubMemberChangeRoleRequest {

    @NotNull @NotBlank
    private ClubMemberRole role;
    @NotNull @NotBlank
    private String email;
    @NotNull @NotBlank
    private String password;
}
