package com.play.hiclear.domain.gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String description;

    private String gymType;
}