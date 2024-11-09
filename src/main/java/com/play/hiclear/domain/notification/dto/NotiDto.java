package com.play.hiclear.domain.notification.dto;

import com.play.hiclear.domain.notification.entity.Noti;
import lombok.*;

public class NotiDto {
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        String id;
        String name;
        String content;
        String type;
        String createdAt;
        public static Response createResponse(Noti notify) {
            return Response.builder()
                    .content(notify.getContent())
                    .id(notify.getId().toString())
                    .type(notify.getNotiType().toString())
                    .name(notify.getReceiver().getName())
                    .createdAt(notify.getCreatedAt().toString())
                    .build();
        }
    }
}
