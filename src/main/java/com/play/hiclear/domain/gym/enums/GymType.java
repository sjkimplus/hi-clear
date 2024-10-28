package com.play.hiclear.domain.gym.enums;

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 타입입니다, [PUBLIC, PRIVATE]중 하나를 입력해주세요"));
    }

}
