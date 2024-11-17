package com.play.hiclear.domain.notification.enums;

import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum NotiType {

    CLUB,
    SCHEDULE,
    BOARD,
    COMMENT;

    public static NotiType of(String type) {
        return Arrays.stream(NotiType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestStateException("유효하지 않은 타입 입니다."));
    }
}
