package com.play.hiclear.domain.gym.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymUpdateRequest {

    private String updateName;
    private String updateDescription;
    private String updateAddress;
}
