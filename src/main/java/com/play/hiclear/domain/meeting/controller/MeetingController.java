package com.play.hiclear.domain.meeting.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.meeting.dto.response.MeetingDetailResponse;
import com.play.hiclear.domain.meeting.dto.response.MyMeetingDetailResponse;
import com.play.hiclear.domain.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    // 번개글 생성
    @PostMapping("/v1/meetings")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser,
                                         @RequestBody MeetingCreateEditRequest request) {
        return ResponseEntity.ok(meetingService.create(authUser, request));
    }

    // 번개글 수정
    @PatchMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser,
                                       @RequestBody MeetingCreateEditRequest request,
                                       @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.update(authUser, request, meetingId));
    }

    // 번개글 삭제
    @DeleteMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.delete(authUser, meetingId));
    }

//    // 번개글 다건 조회 - QueryDSL
//    @GetMapping("/meetings")
//    public ResponseEntity<Page<MeetingSearchResponse>> search(@AuthenticationPrincipal AuthUser authUser,
//                                                                      @RequestParam(defaultValue = "LATEST") SortType sortType,
//                                                                      @RequestParam Ranks rank,
//                                                                      @RequestParam(defaultValue = "1") int page,
//                                                                      @RequestParam(defaultValue = "10") int size) {
//        return ResponseEntity.ok(meetingService.search(authUser, sortType, rank, page, size));
//    }

    // 번개글 단건 조회 + 신천한 번개 단건 조회
    @GetMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailResponse> get(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.get(meetingId));
    }

    // 나의 번개 신청한 번개 다건 조회 - jpql


    // 개최한 번개 단건 조회
    @GetMapping("/v1/my-meetings/{meetingId}")
    public ResponseEntity<MyMeetingDetailResponse> getMyMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMyMeeting(meetingId));
    }

}
