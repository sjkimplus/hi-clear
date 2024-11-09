package com.play.hiclear.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByUserEmail(String receiverId);

    Map<String, Object> findAllEventCacheStartWithByUserEmail(String receiverId);

    void deleteById(String id); //Emitter를 지운다

    void deleteAllEmitterStartWithId(String email);

    void deleteAllEventCacheStartWithId(String email);
}
