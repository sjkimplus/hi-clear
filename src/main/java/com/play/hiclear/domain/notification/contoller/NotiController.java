package com.play.hiclear.domain.notification.contoller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @PatchMapping("/notifications/{notificationId}")
    public ResponseEntity<String> read(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long notificationId) {
        notifyService.read(authUser, notificationId);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.NOTIFICATION_READ));
    }

    @DeleteMapping("/notifications/{notificationId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long notificationId) {
        notifyService.delete(authUser, notificationId);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.NOTIFICATION_READ));
    }
}
