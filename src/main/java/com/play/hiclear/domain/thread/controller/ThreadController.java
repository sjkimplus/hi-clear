package com.play.hiclear.domain.thread.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.thread.dto.request.ThreadCreateRequest;
import com.play.hiclear.domain.thread.dto.request.ThreadDeleteRequest;
import com.play.hiclear.domain.thread.dto.request.ThreadUpdateRequest;
import com.play.hiclear.domain.thread.entity.Thread;
import com.play.hiclear.domain.thread.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    @PostMapping("/v1/comments/{commentId}/threads")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @RequestBody ThreadCreateRequest threadCreateRequest) {
        threadService.create(authUser.getUserId(), commentId, threadCreateRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CREATED, Thread.class.getSimpleName()));
    }

    @PatchMapping("/v1/comments/{commentId}/threads/{threadId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long threadId, @RequestBody ThreadUpdateRequest threadUpdateRequest) throws Exception {
        threadService.update(authUser.getUserId(), threadId, threadUpdateRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.MODIFIED, Thread.class.getSimpleName()));
    }

    @DeleteMapping("/v1/comments/{commentId}/threads/{threadId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long threadId, @RequestBody ThreadDeleteRequest threadDeleteRequest) {
        threadService.delete(authUser.getUserId(), threadId, threadDeleteRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Thread.class.getSimpleName()));
    }
}
