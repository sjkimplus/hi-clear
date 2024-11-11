package com.play.hiclear.domain.notification.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.notification.dto.NotiDto;
import com.play.hiclear.domain.notification.entity.Noti;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.repository.EmitterRepository;
import com.play.hiclear.domain.notification.repository.NotiRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotiService {
    // SSE 연결 지속 시간 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    
    private final EmitterRepository emitterRepository;
    private final NotiRepository notiRepository;
    private final UserRepository userRepository;


    /**
     *
     * @param userEmail  알림을 받을 수신자의 식별 정보
     * @param lastEventId   수신자가 마지막으로 받은 이벤트의 식별자
     * @return  SseEmitter를 생성하여 반환
     */
    @Transactional
    public SseEmitter subscribe(String userEmail, String lastEventId) { // 수신자의 고유 식별 정보와 수신자의 마지막 이벤트 번호를 받는다

        // 유저의 고유 식별자를 이용한 SseEmitter 고유 아이디 생성
        String emitterId = makeTimeIncludeId(userEmail);

        // SseEmitter를 생성하고 emitterId를 키로 사용해 emitterRepository에 저장한다
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // onCompletion(), onTimeout() -> SseEmitter가 완료되거나 타임아웃될 때 해당 SseEmitter를 emitterRepository에서 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // sendNotification을 호출하여 클라이언트에게 연결이 생성되었음을 알리는 더미 메시지 전송하고 이를 통하여 클라이언트-서버 간의 연결이 유지되도록 한다.
        String eventId = makeTimeIncludeId(userEmail);
        sendNotification(emitter, eventId, emitterId, "연결되었습니다. [userEmail = " + userEmail + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            sendLostData(lastEventId, userEmail, emitterId, emitter);
        }

        // 생성된 SseEmitter 객체를 반환하여 클라이언트에게 전달
        // 클라이언트는 이를 통해 서버로부터 알림 이벤트를 수신 / 처리 가능
        return emitter;
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
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0) // 가나다.compareTo("안녕하세요")
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    /**
     *
     * @param receiver 수신자 정보
     * @param notiType  알림 타입
     * @param content   알림 내용
     * @param url   호출한 URL
     */
    @Transactional
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

        // 수신자의 이메일을 receiverEmail 변수에 저장
        String receiverEmail = receiver.getEmail();

        // SsseEmitter로 전송되는 이벤트의 고유 식별자로 사용 된다.
        String eventId = receiverEmail + "_" + System.currentTimeMillis();

        // 수신자에게 연결된 모든 SseEmitter 객체를 emitters 변수에 가져온다(다중 연결을 위한 작업)
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserEmail(receiverEmail);

        // emitter를 순환하며 각 SseEmitter 객체에 알림을 전송한다
        emitters.forEach(
                (key, emitter) -> {
                    // 각 SseEmitter 객체에 이벤트 캐시를 저장한다.
                    emitterRepository.saveEventCache(key, noti);

                    // 알림을 SseEmitter 객체로 전송한다
                    // 생성된 알림 객체를 기반으로 Noti를 Dto로 변환하여 클라이언트에게 알림 전송
                    sendNotification(emitter, eventId, key, NotiDto.createResponse(noti));
                }
        );
    }

    public List<NotiDto> search(String email) {

        //수신자 조회
        User receiver = userRepository.findByEmailAndDeletedAtIsNullOrThrow(email);
        List<Noti> noties = notiRepository.findAllByReceiverIdAndIsReadFalseOrderByIdDesc(receiver.getId());

        return noties.stream().map(NotiDto::createResponse).toList();
    }

    @Transactional
    public void read(String email, Long notificationId) {

        // 알림 조회
        Noti noti = notiRepository.findById(notificationId).orElseThrow(() -> new NullPointerException("존재하지 않는 알림입니다"));

        // 해당 알림 수신자 확인
        if (noti.getReceiver().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Noti.class.getSimpleName());
        }

        noti.read();
    }
}
