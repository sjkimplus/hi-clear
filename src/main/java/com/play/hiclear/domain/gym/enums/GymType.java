package com.play.hiclear.domain.gym.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum GymType {

    PUBLIC("TYPE_PUBLIC"),
    PRIVATE("TYPE_PRIVATE");

    private final String gymType;

    public static GymType of(String type) {
        return Arrays.stream(GymType.values())
                .filter(r -> r.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당타입을"));
    }

}
