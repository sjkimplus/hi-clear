package com.play.hiclear.domain.notification.service;

import com.play.hiclear.domain.notification.dto.NotiDto;
import com.play.hiclear.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseEmitterService {

    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter createEmitter(String emitterKey) {
        return emitterRepository.save(emitterKey, new SseEmitter(DEFAULT_TIMEOUT));
    }

    public void deleteEmitter(String emitterKey) {
        emitterRepository.deleteById(emitterKey);
    }

    public void sendNotificationToClient(String emitterKey, NotiDto notificationDto) {
        emitterRepository.findById(emitterKey)
                .ifPresent(emitter -> send(notificationDto, emitterKey, emitter));
    }

    public void send(Object data, String emitterKey, SseEmitter sseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterKey, data);
            sseEmitter.send(SseEmitter.event()
                    .id(emitterKey)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            emitterRepository.deleteById(emitterKey);
        }
    }
}

