package com.play.hiclear.domain.club.controller;

import com.play.hiclear.domain.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
}
