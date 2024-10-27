package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardSearchDetailResponse {

    private Long id;
    private String title;
    private String context;
    private Long userId;
    private Long clubId;

    public BoardSearchDetailResponse(Long id, String title, String context, Long userId, Long clubId) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.userId = userId;
        this.clubId = clubId;
    }
}
