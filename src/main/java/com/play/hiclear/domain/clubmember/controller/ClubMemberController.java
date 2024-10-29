package com.play.hiclear.domain.clubmember.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.clubmember.dto.ClubMemberChangeRoleRequest;
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

    @PostMapping("/v1/clubs/{clubId}/join")
    public ResponseEntity<String> join(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId) {
        clubMemberService.join(authUser.getUserId(), clubId);
        return ResponseEntity.ok("모임 가입 완료했습니다.");
    }

    @DeleteMapping("/v1/clubs/{clubId}/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId) {
        clubMemberService.withdraw(authUser.getUserId(), clubId);
        return ResponseEntity.ok("모임 탈퇴가 완료되었습니다.");
    }

    @DeleteMapping("/v1/clubs/{clubId}/expel")
    public ResponseEntity<String> clubId(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubMemberExpelRequest clubMemberExpelRequest) {
        clubMemberService.expel(authUser.getUserId(), clubId, clubMemberExpelRequest);
        return ResponseEntity.ok("모임에서 추방했습니다.");
    }

    @PatchMapping("/v1/clubs/{clubId}/role")
    public ResponseEntity<String> change(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubMemberChangeRoleRequest clubMemberChangeRoleRequest) {
        clubMemberService.change(authUser.getUserId(), clubId, clubMemberChangeRoleRequest);
        return ResponseEntity.ok("권한을 변경했습니다.");
    }
}
