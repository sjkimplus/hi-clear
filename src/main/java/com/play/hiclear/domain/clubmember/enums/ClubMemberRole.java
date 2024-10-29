package com.play.hiclear.domain.clubmember.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ClubMemberRole {

    ROLE_MEMBER(ClubMemberAuthority.MEMBER),
    ROLE_MANAGER(ClubMemberAuthority.MANAGER),
    ROLE_ADMIN(ClubMemberAuthority.ADMIN);

    private final String clubMemberRole;

    public static ClubMemberRole of(String role) {
        return Arrays.stream(ClubMemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UerRole"));
    }

    public static class ClubMemberAuthority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}