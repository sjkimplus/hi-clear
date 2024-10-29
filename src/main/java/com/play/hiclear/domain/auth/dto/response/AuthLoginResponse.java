package com.play.hiclear.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLoginResponse {

    private final String bearerToken;
}
