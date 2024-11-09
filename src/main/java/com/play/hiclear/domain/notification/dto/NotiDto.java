package com.play.hiclear.domain.notification.dto;

import com.play.hiclear.domain.notification.entity.Noti;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
public class NotiDto {

    private final String id;
    private final String name;
    private final String content;
    private final String type;
    private final LocalDateTime createdAt;

    public static NotiDto createResponse(Noti notify) {
        return NotiDto.builder()
                .content(notify.getContent())
                .id(notify.getId().toString())
                .type(notify.getNotiType().toString())
                .name(notify.getReceiver().getName())
                .createdAt(notify.getCreatedAt())
                .build();
    }
}
