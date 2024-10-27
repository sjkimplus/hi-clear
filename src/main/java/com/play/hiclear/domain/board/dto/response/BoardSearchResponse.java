package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardSearchResponse {

    private final Long id;
    private final String title;
    private final String context;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public BoardSearchResponse(Long id, String title, String context, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
