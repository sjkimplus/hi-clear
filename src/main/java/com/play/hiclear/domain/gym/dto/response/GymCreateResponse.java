package com.play.hiclear.domain.gym.dto.response;

import com.play.hiclear.domain.gym.enums.GymType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GymCreateResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String address;
    private final GymType gymType;
}
