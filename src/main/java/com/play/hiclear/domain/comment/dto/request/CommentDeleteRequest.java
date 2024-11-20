package com.play.hiclear.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentDeleteRequest {

    @NotBlank
    private String password;

    public CommentDeleteRequest(String password) {
        this.password = password;
    }
}
