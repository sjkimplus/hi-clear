package com.play.hiclear.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.play.hiclear.domain.notification.dto.NotiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisSubscriberService implements MessageListener {

    private final String CHANNEL_PREFIX = "emitter:";

    private final ObjectMapper objectMapper;
    private final SseEmitterService sseEmitterService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel())
                    .substring(CHANNEL_PREFIX.length());

            NotiDto notificationDto = objectMapper.readValue(message.getBody(), NotiDto.class);

            // 클라이언트에게 event 데이터 전송
            sseEmitterService.sendNotificationToClient(channel, notificationDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

