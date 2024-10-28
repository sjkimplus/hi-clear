package com.play.hiclear.domain.auth.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignupResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String region;
    private final Ranks selfRank;
    private final UserRole userRole;

}
