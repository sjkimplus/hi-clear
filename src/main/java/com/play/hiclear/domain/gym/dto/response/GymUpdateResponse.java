package com.play.hiclear.domain.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GymUpdateResponse {

    private final String name;
    private final String description;
    private final String roadAddress;
}
