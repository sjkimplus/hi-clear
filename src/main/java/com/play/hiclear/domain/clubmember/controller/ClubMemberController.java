package com.play.hiclear.domain.clubmember.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.clubmember.dto.request.ClubMemberChangeRoleRequest;
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
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CLUBMEMBER_JOIN));
    }

    @DeleteMapping("/v1/clubs/{clubId}/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId) {
        clubMemberService.withdraw(authUser.getUserId(), clubId);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CLUBMEMBER_WITHDRAW));
    }

    @DeleteMapping("/v1/clubs/{clubId}/expel")
    public ResponseEntity<String> clubId(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubMemberExpelRequest clubMemberExpelRequest) {
        clubMemberService.expel(authUser.getUserId(), clubId, clubMemberExpelRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CLUBMEMBER_EXPEL));
    }

    @PutMapping("/v1/clubs/{clubId}/role")
    public ResponseEntity<String> change(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubMemberChangeRoleRequest clubMemberChangeRoleRequest) {
        clubMemberService.change(authUser.getUserId(), clubId, clubMemberChangeRoleRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CLUBMEMBER_CHANGE_ROLE));
    }
}
