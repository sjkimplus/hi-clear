package com.play.hiclear.domain.board.dto.request;

import lombok.Getter;


@Getter
public class BoardCreateRequest {
    private String title;
    private String context;
    private Long userId;

}
