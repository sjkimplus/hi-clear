package com.play.hiclear.domain.clubmember.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.clubmember.dto.request.ClubMemberExpelRequest;
import com.play.hiclear.domain.clubmember.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @PostMapping("/v1/clubs/{clubsId}/join")
    public ResponseEntity<String> join(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubsId) {
        clubMemberService.join(authUser.getUserId(), clubsId);
        return ResponseEntity.ok("모임 가입 완료했습니다.");
    }

    @DeleteMapping("/v1/clubs/{clubsId}/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubsId) {
        clubMemberService.withdraw(authUser.getUserId(), clubsId);
        return ResponseEntity.ok("모임 탈퇴가 완료되었습니다.");
    }

    @DeleteMapping("/v1/clubs/{clubsId}/expel")
    public ResponseEntity<String> expel(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubsId, @RequestBody ClubMemberExpelRequest clubMemberExpelRequest) {
        clubMemberService.expel(authUser.getUserId(), clubsId, clubMemberExpelRequest);
        return ResponseEntity.ok("모임에서 추방했습니다.");
    }
}
