package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardCreateResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long userId;
    private final Long clubId;


    public BoardCreateResponse(Long id, String title, String content, Long userId, Long clubId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.clubId = clubId;
    }
}
