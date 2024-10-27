package com.play.hiclear.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER(Authority.USER),
    BUSINESS(Authority.BUSINESS);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다, [USER, BUSINESS]중 하나를 입력해주세요"));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String BUSINESS = "ROLE_BUSINESS";
    }
}