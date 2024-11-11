package com.play.hiclear.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardUpdateRequest {
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    private String title;
    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    private String context;
}
