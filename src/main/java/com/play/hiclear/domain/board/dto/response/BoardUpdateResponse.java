package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardUpdateResponse {
    private Long id;
    private String title;
    private String context;

    public BoardUpdateResponse(Long id, String title, String context) {
        this.id = id;
        this.title = title;
        this.context = context;
    }
}
