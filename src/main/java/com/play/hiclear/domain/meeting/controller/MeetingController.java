package com.play.hiclear.domain.meeting.controller;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.dto.request.MeetingUpdateRequest;
import com.play.hiclear.domain.meeting.dto.response.*;
import com.play.hiclear.domain.meeting.entity.MeetingDocument;
import com.play.hiclear.domain.meeting.enums.SortType;
import com.play.hiclear.domain.meeting.service.MeetingDummyData;
import com.play.hiclear.domain.meeting.service.MeetingService;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingDummyData meetingDummyData;

    @PostMapping("/v1/meetings/dummy")
    public void testDummy() {
        meetingDummyData.generateDummyMeetings();
    }

    // 인덱스를 적용하기 전 검색
    @GetMapping("/v1/search")
    public ResponseEntity<Page<MeetingDocumentResponse>> searchBeforeMeetings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String regionAddress,
            @RequestParam(required = false) String ranks,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 서비스 메서드 호출
        Page<MeetingDocumentResponse> meetings = meetingService.searchBeforeMeetings(title, regionAddress, ranks, page, size);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/v2/search")
    public ResponseEntity<Page<MeetingDocument>> searchMeetings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String regionAddress,
            @RequestParam(required = false) String ranks,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 서비스 메서드 호출
        Page<MeetingDocument> meetings = meetingService.searchMeetings(title, regionAddress, ranks, page, size);
        return ResponseEntity.ok(meetings);
    }

    // 번개글 생성
    @PostMapping("/v1/meetings")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser,
                                         @RequestBody @Valid MeetingCreateRequest request) {
        return ResponseEntity.ok(meetingService.create(authUser, request));
    }

    // 번개글 수정
    @PatchMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser,
                                       @RequestBody @Valid MeetingUpdateRequest request,
                                       @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.update(authUser, request, meetingId));
    }

    // 번개글 삭제
    @DeleteMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.delete(authUser, meetingId));
    }

    // 번개글 다건 조회
    @GetMapping("/v1/meetings")
    public ResponseEntity<Page<MeetingSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "LATEST") SortType sortType,
            @RequestParam(required = false) Ranks ranks,
            @RequestParam int distance,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate distance
        if (distance != 5 && distance != 10 && distance != 50 && distance != 100) {
            throw new CustomException(ErrorCode.INVALID_DISTANCE);
        }

        return ResponseEntity.ok(meetingService.search(sortType, ranks, distance, page, size, authUser));
    }


    // 번개글 단건 조회 + 신청한 번개 단건 조회
    @GetMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailResponse> get(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.get(meetingId));
    }

    // 나의 번개 (신청/개최) 다건 조회 - 구분은 role 이 HOST/GUEST로 구분
    @GetMapping("/v1/my-meetings")
    public ResponseEntity<MyMeetingResponses> searchMyMeetings(@AuthenticationPrincipal AuthUser authUser,
                                                               @RequestParam ParticipantRole role){
        return ResponseEntity.ok(meetingService.searchMyMeetings(authUser, role));
    }

    // 개최한 번개 단건 조회
    @GetMapping("/v1/my-meetings/{meetingId}")
    public ResponseEntity<MyMeetingDetailResponse> getMyMeeting(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMyMeeting(authUser, meetingId));
    }

    // 나의 번개 활동 완료
    @PutMapping("v1/my-meetings/{meetingId}")
    public ResponseEntity<String> finishMyMeeting(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.finishMyMeeting(authUser, meetingId));
    }

}

