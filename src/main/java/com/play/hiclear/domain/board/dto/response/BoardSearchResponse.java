package com.play.hiclear.domain.board.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardSearchResponse {

    private final Long id;
    private final String title;
    private final String context;
    // 작성자의 정보
    private final Long userId;
    private final String userName;
    private final String userEmail;

    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public BoardSearchResponse(Long id, String title, String context, Long userId, String userName, String userEmail, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
