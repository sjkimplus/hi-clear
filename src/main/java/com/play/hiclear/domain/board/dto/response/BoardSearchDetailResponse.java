package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardSearchDetailResponse {

    private Long id;
    private String title;
    private String context;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long clubId;

    public BoardSearchDetailResponse(Long id, String title, String context, Long userId, String userName, String userEmail, Long clubId) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.clubId = clubId;
    }
}
