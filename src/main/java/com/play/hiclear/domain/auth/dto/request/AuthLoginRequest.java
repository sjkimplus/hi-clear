package com.play.hiclear.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    public AuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
