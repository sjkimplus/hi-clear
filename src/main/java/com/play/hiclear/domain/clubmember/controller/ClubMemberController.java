package com.play.hiclear.domain.clubmember.controller;

import com.play.hiclear.domain.clubmember.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;
}
