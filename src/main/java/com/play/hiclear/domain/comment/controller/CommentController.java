package com.play.hiclear.domain.comment.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.comment.dto.CommentCreateRequest;
import com.play.hiclear.domain.comment.dto.CommentDeleteRequest;
import com.play.hiclear.domain.comment.dto.CommentUpdateRequest;
import com.play.hiclear.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/v1/clubboards/{clubboardsId}/comments")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubboardsId, @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService.create(authUser.getUserId(), clubboardsId, commentCreateRequest);
        return ResponseEntity.ok("댓글을 작성하였습니다");
    }

    @PatchMapping("/v1/clubboards/{clubboardsId}/comments/{commentId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @RequestBody CommentUpdateRequest commentUpdateRequest) throws Exception {
        commentService.update(authUser.getUserId(), commentId, commentUpdateRequest);
        return ResponseEntity.ok("댓글 수정이 완료되었습니다");
    }

    @DeleteMapping("/v1/clubboards/{clubboardsId}/comments/{commentId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        commentService.delete(authUser.getUserId(), commentId, commentDeleteRequest);
        return ResponseEntity.ok("댓글을 삭제했습니다.");
    }
}
