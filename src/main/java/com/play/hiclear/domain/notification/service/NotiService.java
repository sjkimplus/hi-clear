package com.play.hiclear.domain.notification.service;

import com.play.hiclear.domain.notification.dto.NotiDto;
import com.play.hiclear.domain.notification.entity.Noti;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.repository.NotiRepository;
import com.play.hiclear.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotiService {

    private final NotiRepository notiRepository;
    private final SseEmitterService sseEmitterService;
    private final RedisMessageService redisMessageService;

    public SseEmitter subscribe(String userEmail) {

        SseEmitter sseEmitter = sseEmitterService.createEmitter(userEmail);
        sseEmitterService.send("EventStream Created.", userEmail, sseEmitter); // send dummy

        redisMessageService.subscribe(userEmail); // redis 구독

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());

        //
        sseEmitter.onCompletion(() -> {
            sseEmitterService.deleteEmitter(userEmail);
            redisMessageService.removeSubscribe(userEmail);
        });
        return sseEmitter;
    }

    @Transactional
    public void sendNotification(User receiver, NotiType notiType, String content, String relatedUrl) {

        Noti noti = notiRepository.save(Noti.builder()
                        .content(content)
                        .url(relatedUrl)
                        .isRead(false)
                        .notiType(notiType)
                        .receiver(receiver)
                        .build()
                );

        // redis 이벤트 발행
        redisMessageService.publish(receiver.getEmail(), NotiDto.from(noti));
    }
}
