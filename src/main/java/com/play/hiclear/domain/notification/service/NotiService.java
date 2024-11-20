package com.play.hiclear.domain.notification.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.notification.dto.NotiDto;
import com.play.hiclear.domain.notification.entity.Noti;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.repository.NotiRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.Builder;
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
    private final UserRepository userRepository;

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
    @Builder
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

    @Transactional
    public void read(AuthUser authUser, Long notificationId) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Noti noti = notiRepository.findByIdOrThrow(notificationId);

        if (!noti.getReceiver().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Noti.class.getSimpleName());
        }

        noti.read();
    }

    @Transactional
    public void delete(AuthUser authUser, Long notificationId) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Noti noti = notiRepository.findByIdOrThrow(notificationId);

        if (!noti.getReceiver().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Noti.class.getSimpleName());
        }

        notiRepository.delete(noti);
    }
}
