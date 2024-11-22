package com.play.hiclear.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateRequest {

    @NotBlank
    private String content;
    @NotBlank
    private String password;

    public CommentUpdateRequest(String content, String password) {
        this.content = content;
        this.password = password;
    }
}
