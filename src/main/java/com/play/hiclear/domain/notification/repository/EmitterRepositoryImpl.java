package com.play.hiclear.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * EmitterRepository 인터페이스를 구현한 클래스
 */
@Repository
@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository{

    /**
     * ConcurrentHashMap
     * 스레드 안전(thread-safe)한 맵
     * 동시에 여러 스레드가 접근하더라도 안전하게 데이터를 조작할 수 있도록 보장
     * 이 맵을 사용하여 동시성 문제를 해결하고 맵에 데이터 저장 및 조회가 가능하다
     */

    /**
     * emitters
     * String 타입의 키와 SseEmitter 타입의 값으로 이루어져있다.
     * 사용자 별로 생성된 SseEmitter 객체를 저장하는 역할이다.
     * 클라이언트가 구독을 요청하면 해당 사용자의 식별자를 키로 사용하여 맵에 저장되고
     * 이후 알림을 전송할 때 해당 사용자의 SseEmitter를 조회하기 위해 사용된다.
     */
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public Optional<SseEmitter> findById(String memberId) {
        return Optional.ofNullable(emitters.get(memberId));
    }

    @Override
    public void deleteById(String eventId) {
        emitters.remove(eventId);
    }
}
