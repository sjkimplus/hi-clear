package com.play.hiclear.domain.notification.contoller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotiController {

    private final NotiService notifyService;

    /**
     *
     * @param authUser  접속하려는 사용자의 ID
     * @param lastEventId   사용자가 마지막으로 받은 알림의 번호, 항상 담겨있는 것은 아니고 연결이 끊어졌을 때
     *                      클라이언트에 도달하지 못한 알림이 있을 때 이를 이용하여 유실된 데이터를 다시 보낼 수 있다.
     * @return
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUser authUser,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notifyService.subscribe(authUser.getEmail(), lastEventId);
    }
}