package com.play.hiclear.domain.alarm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AlarmType {

    JOIN("맴버를 추가하였습니다."),
    SCHEDULE("카드를 변경하였습니다."),
    COMMENT("댓글을 추가하였습니다.");

    private final String message;

    public static AlarmType of(String type) {
        return Arrays.stream(AlarmType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 type"));
    }

}
