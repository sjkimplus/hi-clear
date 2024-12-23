package com.play.hiclear.domain.club.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubGetResponse;
import com.play.hiclear.domain.club.dto.response.ClubNearResponse;
import com.play.hiclear.domain.club.dto.response.ClubSearchResponse;
import com.play.hiclear.domain.club.dto.response.ClubUpdateResponse;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.entity.ClubDocument;
import com.play.hiclear.domain.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/v1/clubs")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @RequestBody ClubCreateRequest clubCreateRequest) {
        clubService.create(authUser.getUserId(), clubCreateRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.CREATED, Club.class.getSimpleName()));
    }

    @GetMapping("/v1/clubs/{clubId}")
    public ResponseEntity<ClubGetResponse> get(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.get(clubId));
    }

    @PutMapping("/v1/clubs/{clubId}")
    public ResponseEntity<ClubUpdateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestBody ClubUpdateRequest clubUpdateRequest
    ) {
        return ResponseEntity.ok(clubService.update(authUser.getUserId(), clubId, clubUpdateRequest));
    }

    @GetMapping("/v1/clubs")
    public ResponseEntity<Page<ClubSearchResponse>> search(
            @RequestParam(required = false) String clubname,
            @RequestParam(required = false) String intro,
            @RequestParam(required = false) String regionAddress,
            @RequestParam(required = false) String roadAddress,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clubService.search(page, size, clubname, intro, regionAddress, roadAddress));
    }

    @DeleteMapping("/v1/clubs/{clubId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubDeleteRequest clubDeleteRequest) {
        clubService.delete(authUser.getUserId(), clubId, clubDeleteRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Club.class.getSimpleName()));
    }

    @GetMapping("/v1/clubs/search")
    public ResponseEntity<Page<ClubDocument>> elsearch(
            @RequestParam(required = false) String clubname,
            @RequestParam(required = false) String intro,
            @RequestParam(required = false) String regionAddress,
            @RequestParam(required = false) String roadAddress,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clubService.elsearch(page, size, clubname, intro, regionAddress, roadAddress));
    }

    @PostMapping("/v1/clubs/dummy")
    public ResponseEntity<String> createDummy(){
        clubService.createDummy();
        return ResponseEntity.ok("더미데이터생성");
    }

    @GetMapping("/v1/clubs/near")
    public ResponseEntity<Page<ClubNearResponse>> near(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) Double distance,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clubService.near(authUser, distance, page, size));
    }
}
