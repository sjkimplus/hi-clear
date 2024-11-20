package com.play.hiclear.domain.comment.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.comment.dto.request.CommentCreateRequest;
import com.play.hiclear.domain.comment.dto.request.CommentDeleteRequest;
import com.play.hiclear.domain.comment.dto.request.CommentUpdateRequest;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/v1/clubboards/{clubboardId}/comments")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubboardId, @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService.create(authUser.getUserId(), clubboardId, commentCreateRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CREATED, Comment.class.getSimpleName()));
    }

    @PutMapping("/v1/clubboards/{clubboardId}/comments/{commentId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.update(authUser.getUserId(), commentId, commentUpdateRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.MODIFIED, Comment.class.getSimpleName()));
    }

    @DeleteMapping("/v1/clubboards/{clubboardId}/comments/{commentId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        commentService.delete(authUser.getUserId(), commentId, commentDeleteRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Comment.class.getSimpleName()));
    }
}
