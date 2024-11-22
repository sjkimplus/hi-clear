package com.play.hiclear.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthSignupRequest {

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식을 맞춰주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$",
            message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자 포함, 최소 8글자 이상")
    @NotBlank(message = "비밀번호는 필수사항입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 사항 입니다.")
    private String name;

    private String address;

    @NotBlank(message = "권한 선택은 필수사항입니다.")
    private String selfRank;

    @NotBlank(message = "권한 선택은 필수사항입니다.")
    private String userRole;

    public AuthSignupRequest(String email, String password, String name, String address, String selfRank, String userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.selfRank = selfRank;
        this.userRole = userRole;
    }
}
