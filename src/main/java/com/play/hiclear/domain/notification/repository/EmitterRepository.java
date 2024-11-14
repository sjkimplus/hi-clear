package com.play.hiclear.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    Optional<SseEmitter> findById(String emitterId);
    void deleteById(String id); //Emitter를 지운다

}
