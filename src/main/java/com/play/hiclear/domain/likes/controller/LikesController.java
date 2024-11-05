package com.play.hiclear.domain.likes.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    // 좋아요 토글 기능
    @PostMapping("/v1/comments/{commentId}/likes")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthUser authUser
    ){
        likesService.toggleLike(commentId, authUser);
        return ResponseEntity.ok().build();//성공하면 200 OK를 반환
    }

    // 좋아요 조회 기능
    @GetMapping("/v1/comments/{commentId}/likes/status")
    public ResponseEntity<Long> get(
            @PathVariable Long commentId
    ){
        return ResponseEntity.ok(likesService.get(commentId));
    }
}
