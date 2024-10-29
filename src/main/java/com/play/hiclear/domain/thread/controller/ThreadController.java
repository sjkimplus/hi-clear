package com.play.hiclear.domain.thread.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.thread.dto.ThreadCreateRequest;
import com.play.hiclear.domain.thread.dto.ThreadDeleteRequest;
import com.play.hiclear.domain.thread.dto.ThreadUpdateRequest;
import com.play.hiclear.domain.thread.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    @PostMapping("/v1/comments/{commentsId}/threads")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentsId, @RequestBody ThreadCreateRequest threadCreateRequest) {
        threadService.create(authUser.getUserId(), commentsId, threadCreateRequest);
        return ResponseEntity.ok("대댓글을 작성하였습니다");
    }

    @PatchMapping("/v1/comments/{commentsId}/threads/{threadsId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long threadsId, @RequestBody ThreadUpdateRequest threadUpdateRequest) throws Exception {
        threadService.update(authUser.getUserId(), threadsId, threadUpdateRequest);
        return ResponseEntity.ok("대댓글 수정이 완료되었습니다");
    }

    @DeleteMapping("/v1/comments/{commentsId}/threads/{threadsId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long threadsId, @RequestBody ThreadDeleteRequest threadDeleteRequest) {
        threadService.delete(authUser.getUserId(), threadsId, threadDeleteRequest);
        return ResponseEntity.ok("대댓글을 삭제했습니다.");
    }
}
