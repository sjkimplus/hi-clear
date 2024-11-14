package com.play.hiclear.domain.notification.contoller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotiController {

    private final NotiService notifyService;

    /**
     *
     * @param authUser  접속하려는 사용자의 ID
     * @return
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUser authUser) {
        return notifyService.subscribe(authUser.getEmail());
    }
}
