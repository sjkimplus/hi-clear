package com.play.hiclear.domain.likes.controller;

import com.play.hiclear.domain.likes.dto.response.LikesSearchResponse;
import com.play.hiclear.domain.likes.entity.Likes;
import com.play.hiclear.domain.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private LikesService likesService;

    // 좋아요 토글 기능
    @PostMapping("/v1/clubs/{clubId}/clubboards/{clubboardId}/comments/{commentId}/likes")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long clubId,
            @PathVariable Long clubboardId,
            @PathVariable Long commentId,
            @RequestParam Long userId
    ){
        likesService.toggleLike(clubId, clubboardId, commentId, userId);
        return ResponseEntity.ok().build();//성공하면 200 OK를 반환
    }

    // 좋아요 조회 기능
    @GetMapping("/v1/clubs/{clubId}/clubboards/{clubboardId}/comments/{commentId}/likes/status")
    public ResponseEntity<List<LikesSearchResponse>> get(
            @PathVariable Long clubId,
            @PathVariable Long clubboardId,
            @PathVariable Long commentId
    ){
        return ResponseEntity.ok(likesService.get(commentId));
    }
}
