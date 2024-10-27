package com.play.hiclear.domain.board.controller;

import com.play.hiclear.domain.board.dto.request.BoardCreateRequest;
import com.play.hiclear.domain.board.dto.request.BoardUpdateRequest;
import com.play.hiclear.domain.board.dto.response.BoardCreateResponse;
import com.play.hiclear.domain.board.dto.response.BoardSearchDetailResponse;
import com.play.hiclear.domain.board.dto.response.BoardSearchResponse;
import com.play.hiclear.domain.board.dto.response.BoardUpdateResponse;
import com.play.hiclear.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 보드 생성
    @PostMapping("/v1/clubs/{clubId}/clubboards")
    public ResponseEntity<BoardCreateResponse> create(@PathVariable Long clubId,
                                                           @RequestBody BoardCreateRequest request) {
        return ResponseEntity.ok(boardService.create(clubId, request));
    }

    // 보드 다건 조회
    @GetMapping("/v1/clubs/{clubId}/clubboards")
    public ResponseEntity<Page<BoardSearchResponse>> search(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size
    ){
        return ResponseEntity.ok(boardService.search(clubId, page, size));
    }

    // 보드 단건 조회
    @GetMapping("/v1/clubs/{clubId}/clubboards/{clubboardId}")
    public ResponseEntity<BoardSearchDetailResponse> get(
            @PathVariable Long clubId,
            @PathVariable Long clubboardId
    ){
        return ResponseEntity.ok(boardService.get(clubId, clubboardId));
    }

    // 보드 수정
    @PatchMapping("/v1/clubs/{clubId}/clubboards/{clubboardId}")
    public ResponseEntity<BoardUpdateResponse> update(
            @PathVariable Long clubId,
            @PathVariable Long clubboardId,
            @RequestBody BoardUpdateRequest request
    ){
        return ResponseEntity.ok(boardService.update(clubId, clubboardId, request));
    }

    @DeleteMapping("/v1/clubs/{clubId}/clubboards/{clubboardId}")
    public void delete(
            @PathVariable Long clubId,
            @PathVariable Long clubboardId
    ){
        boardService.delete(clubId, clubboardId);
    }
}
