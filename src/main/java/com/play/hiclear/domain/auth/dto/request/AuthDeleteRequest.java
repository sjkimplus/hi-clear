package com.play.hiclear.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthDeleteRequest {

    private String password;

    public AuthDeleteRequest(String password) {
        this.password = password;
    }
}
