package com.play.hiclear.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.play.hiclear.domain.notification.entity.Noti;
import com.play.hiclear.domain.notification.enums.NotiType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotiDto {

    private Long receiverId;
    private String content;
    private String relatedUrl;
    private boolean isRead;
    private NotiType notiType;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static NotiDto from(Noti noti) {
        return new NotiDto(noti.getReceiver().getId(), noti.getContent(),
                noti.getUrl(), noti.getIsRead(), noti.getNotiType(),noti.getCreatedAt());
    }
}
