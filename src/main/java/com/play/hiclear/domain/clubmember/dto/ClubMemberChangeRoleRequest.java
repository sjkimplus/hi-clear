package com.play.hiclear.domain.clubmember.dto;

import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import lombok.Getter;

@Getter
public class ClubMemberChangeRoleRequest {

    private ClubMemberRole role;
    private String email;
    private String password;
}
