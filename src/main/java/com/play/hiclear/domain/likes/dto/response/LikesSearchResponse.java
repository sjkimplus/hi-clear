package com.play.hiclear.domain.likes.dto.response;

public class LikesSearchResponse {
    private Long userId;
    private boolean status;


    public LikesSearchResponse(Long userId, boolean status) {
        this.userId = userId;
        this.status = status;
    }
}
