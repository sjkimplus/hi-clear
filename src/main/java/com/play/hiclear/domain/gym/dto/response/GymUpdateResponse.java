package com.play.hiclear.domain.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GymUpdateResponse {

    private String name;
    private String description;
    private String address;
}
