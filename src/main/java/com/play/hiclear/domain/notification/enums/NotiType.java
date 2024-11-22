package com.play.hiclear.domain.notification.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;

import java.util.Arrays;

public enum NotiType {

    CLUB,
    SCHEDULE,
    MEETING,
    BOARD,
    COMMENT;

    public static NotiType of(String type) {
        return Arrays.stream(NotiType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOTI_BAD_REQUEST_TYPE));
    }
}
