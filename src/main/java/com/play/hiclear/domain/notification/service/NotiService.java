package com.play.hiclear.domain.notification.service;

import com.play.hiclear.domain.notification.dto.NotiDto;
import com.play.hiclear.domain.notification.entity.Noti;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.repository.EmitterRepository;
import com.play.hiclear.domain.notification.repository.NotiRepository;
import com.play.hiclear.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotiService {
    // SSE 연결 지속 시간 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    
    private final EmitterRepository emitterRepository;
    private final NotiRepository notiRepository;


    /**
     *
     * @param username  알림을 받을 수신자의 식별 정보
     * @param lastEventId   수신자가 마지막으로 받은 이벤트의 식별자
     * @return  SseEmitter를 생성하여 반환
     */
    public SseEmitter subscribe(String username, String lastEventId) { // 수신자의 고유 식별 정보와 수신자의 마지막 이벤트 번호를 받는다

        // 유저의 고유 식별자를 이용한 SseEmitter 고유 아이디 생성
        String emitterId = makeTimeIncludeId(username);
        // SseEmitter를 생성하고 emitterId를 키로 사용해 emitterRepository에 저장한다
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        // onCompletion(), onTimeout() -> SseEmitter가 완료되거나 타임아웃될 때 해당 SseEmitter를 emitterRepository에서 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        // sendNotification을 호출하여 클라이언트에게 연결이 생성되었음을 알리는 더미 메시지 전송하고 이를 통하여 클라이언트-서버 간의 연결이 유지되도록 한다.
        String eventId = makeTimeIncludeId(username);
        sendNotification(emitter, eventId, emitterId, "연결되었습니다. [userEmail = " + username + "]");

        // (1-6) 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            sendLostData(lastEventId, username, emitterId, emitter);
        }
        return emitter; // (1-7)
    }

    /**
     *
     * @eamil 메시지를 수신 받을 유저의 고유 식별자
     * @return  SseEmitter를 식별하기 위한 고유 아이디 반환
     */
    private String makeTimeIncludeId(String email) { // (3)
        return email + "_" + System.currentTimeMillis();
    }
    
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) { // (4)
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) { // (6)
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserEmail(userEmail);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }
    
    // [2] send()
    //@Override
    public void send(User receiver, NotiType notiType, String content, String url) {

        // 알림 객체 생성 및 저장
        Noti noti = notiRepository.save(
                Noti.builder()
                        .receiver(receiver)
                        .notiType(notiType)
                        .content(content)
                        .url(url)
                        .isRead(false)
                        .build()
                );

        String receiverEmail = receiver.getEmail(); // (2-2)
        String eventId = receiverEmail + "_" + System.currentTimeMillis(); // (2-3)
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserEmail(receiverEmail); // (2-4)
        emitters.forEach( // (2-5)
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, noti);
                    sendNotification(emitter, eventId, key, NotiDto.Response.createResponse(noti));
                }
        );
    }
}
