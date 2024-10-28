package com.play.hiclear.domain.user.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_BAD_REQUEST_ROLE));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String BUSINESS = "ROLE_BUSINESS";
    }
}