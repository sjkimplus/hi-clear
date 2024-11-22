package com.play.hiclear.domain.clubmember.dto.request;

import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClubMemberChangeRoleRequest {

    @NotBlank
    private ClubMemberRole role;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
