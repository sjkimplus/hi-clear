package com.play.hiclear.domain.notification.service;

import com.play.hiclear.domain.notification.dto.NotiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisMessageService {
    private final String CHANNEL_PREFIX = "emitter:";

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriberService redisSubscriberService; // 따로 구현한 Subscriber
    private final RedisTemplate<String, Object> redisTemplate;

    // 채널 구독
    public void subscribe(String channel) {
        redisMessageListenerContainer.addMessageListener(redisSubscriberService, ChannelTopic.of(getChannelName(channel)));
    }

    // 이벤트 발행
    public void publish(String channel, NotiDto notificationDto) {
        redisTemplate.convertAndSend(getChannelName(channel), notificationDto);
    }

    // 구독 삭제
    public void removeSubscribe(String channel) {
        redisMessageListenerContainer.removeMessageListener(redisSubscriberService, ChannelTopic.of(getChannelName(channel)));
    }

    private String getChannelName(String id) {
        return CHANNEL_PREFIX + id;
    }
}
