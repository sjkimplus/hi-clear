package com.play.hiclear.domain.clubmember.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ClubMemberRole {

    ROLE_MEMBER(ClubMemberAuthority.MEMBER),
    ROLE_MANAGER(ClubMemberAuthority.MANAGER),
    ROLE_MASTER(ClubMemberAuthority.MASTER);

    private final String clubMemberRole;

    public static ClubMemberRole of(String role) {
        return Arrays.stream(ClubMemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CLUBMEBER_BAD_REQUEST_TYPE));
    }

    public static class ClubMemberAuthority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}