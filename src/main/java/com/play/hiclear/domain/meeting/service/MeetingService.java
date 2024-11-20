package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.dto.request.MeetingUpdateRequest;
import com.play.hiclear.domain.meeting.dto.response.*;
import com.play.hiclear.domain.meeting.entity.MeetingDocument;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.enums.SortType;
import com.play.hiclear.domain.meeting.repository.MeetingElasticSearchRepository;
import com.play.hiclear.domain.meeting.repository.MeetingQueryDslRepository;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.participant.service.ParticipantService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true) // 수정이 없는 데이터는 읽기만 함.
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final MeetingQueryDslRepository meetingQueryDslRepository;
    private final GeoCodeService geoCodeService;
    private final MeetingElasticSearchRepository meetingESRepository;

    /**
     * @param title
     * @param regionAddress
     * @param ranks
     * @param page
     * @param size
     * @return
     */
    public Page<MeetingDocument> searchMeetings(String title, String regionAddress, String ranks, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // 급수(Ranks)로 필터링
        if (ranks != null && !ranks.isEmpty()) {
            return meetingESRepository.findByRanks(ranks, pageable);
        }
        // 제목과 지역주소를 모두 검색
        else if (title != null && !title.isEmpty() && regionAddress != null && !regionAddress.isEmpty()) {
            return meetingESRepository.findByTitleContainingOrRegionAddressContaining(title, regionAddress, pageable);
        }
        // 제목만 검색
        else if (title != null && !title.isEmpty()) {
            return meetingESRepository.findByTitleContaining(title, pageable);
        }
        // 지역주소만 검색
        else if (regionAddress != null && !regionAddress.isEmpty()) {
            return meetingESRepository.findByRegionAddressContaining(regionAddress, pageable);
        }
        // 모든 검색 조건이 없으면, 전체 데이터를 페이징하여 반환
        else {
            return meetingESRepository.findAll(pageable);
        }
    }


    /**
     * 인덱스를 적용하기 전 검색
     *
     * @param title
     * @param regionAddress
     * @param ranks
     * @param page
     * @param size
     * @return
     */
    public Page<MeetingDocumentResponse> searchBeforeMeetings(String title, String regionAddress, String ranks, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 급수(Ranks)로 필터링
        if (ranks != null && !ranks.isEmpty()) {
            // Fetch meetings with ranks and apply pagination
            Page<Meeting> meetings = meetingRepository.findByRanks(Ranks.of(ranks), pageable);

            // Convert Meeting entities to MeetingDocumentResponse
            Page<MeetingDocumentResponse> meetingResponses = meetings.map(meeting -> new MeetingDocumentResponse(meeting));

            return meetingResponses;
        }

        // // 제목 또는 지역주소를 검색
        if ((title != null && !title.isEmpty()) || (regionAddress != null && !regionAddress.isEmpty())) {
            Page<Meeting> meetings = meetingRepository.findByTitleContainingOrRegionAddressContaining(title, regionAddress, pageable);

            Page<MeetingDocumentResponse> meetingResponses = meetings.map(meeting -> new MeetingDocumentResponse(meeting));

            return meetingResponses;
        }

        // 아무것도 없으면 전체 반환
        Page<Meeting> meetings = meetingRepository.findAll(pageable);

        return meetings.map(meeting -> new MeetingDocumentResponse(meeting));
    }

    /**
     * 번개 생성
     * @param authUser
     * @param request
     * @return
     */
    @Transactional
    public String create(AuthUser authUser, MeetingCreateRequest request) {
        // 시간 체크 - 최소 한시간, 시작시간은 현재 이후만 가능
        checkTimeValidity(request.getStartTime(), request.getEndTime());
        User user =     userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        // 주소값 가져오기
        GeoCodeDocument address = geoCodeService.getGeoCode(request.getAddress());

        Meeting meeting = new Meeting(request, user, address);
        meetingRepository.save(meeting);
        // ES에 저장
        Meeting foundMeeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId());
        MeetingDocument meetingDocument = new MeetingDocument(foundMeeting);
        meetingESRepository.save(meetingDocument);

        // participant 에 추가
        participantService.add(authUser, meeting.getId());
        return SuccessMessage.customMessage(SuccessMessage.CREATED, Meeting.class.getSimpleName());
    }

    /**
     * 번개 수정
     * @param authUser
     * @param request
     * @param meetingId
     * @return
     */
    @Transactional
    public String update(AuthUser authUser, MeetingUpdateRequest request, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 시간 체크 - 최소 한시간, 시작시간은 현재 이후만 가능
        if (request.getStartTime()!=null || request.getEndTime()!=null) {
            checkTimeValidity(request.getStartTime(), request.getEndTime());
        }

        // 권한체크 - 작성자가 맞는지 체크
        checkAuthority(authUser, meeting);
        meeting.update(request);
        // 기존에 ES에 있는 데이터 삭제
        meetingESRepository.deleteById(meeting.getId());
        // ES에 저장
        // not sure
        MeetingDocument meetingDocument = new MeetingDocument(meeting);
        meetingESRepository.save(meetingDocument);


        // 지역 정보(region)가 제공되면, 해당 정보를 사용하여 위치 정보를 갱신
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            GeoCodeDocument address = geoCodeService.getGeoCode(request.getAddress());

            // 도로명 주소 또는 지번 주소가 존재하면 위치 정보 갱신
            if (address.getRegionAddress() != null || address.getRoadAddress() != null) {
                meeting.updateLocation(address);
            }
        }
        return SuccessMessage.customMessage(SuccessMessage.MODIFIED, Meeting.class.getSimpleName());
    }

    /**
     * 번개 삭제
     * @param authUser
     * @param meetingId
     * @return
     */
    @Transactional
    public String delete(AuthUser authUser, Long meetingId) {
        // 서비스내에서 private method으로 빼기
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크 - 작성자가 맞는지 체크
        checkAuthority(authUser, meeting);

        meeting.markDeleted();
        meetingESRepository.deleteById(meeting.getId());
        return SuccessMessage.customMessage(SuccessMessage.DELETED, Meeting.class.getSimpleName());
    }

    /**
     * 번개글 단건 조회 + 신청한 번개 단건 조회 (특별 권한 없는 일반 조회)
     * @param meetingId
     * @return
     */
    public MeetingDetailResponse get(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 참여 완료한 사람수 구하기
        int numberJoined = participantService.getJoinedNumber(meeting);
        return new MeetingDetailResponse(meeting, numberJoined);
    }

    /**
     * 개최한 번개 단건 조회
     * @param authUser
     * @param meetingId
     * @return
     */
    public MyMeetingDetailResponse getMyMeeting(AuthUser authUser, Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크
        checkAuthority(authUser, meeting);

        // 번개 신청자들 중 APPROVE 된 신청자들 수
        int numberJoined = participantService.getJoinedNumber(meeting);

        // 번개 신청자들 중 PENDING 상태인 것들 모으기
        List<ParticipantResponse> pendingParticipants = participantService.getPendingParticipants(meeting);

        return new MyMeetingDetailResponse(meeting, numberJoined, pendingParticipants);
    }

    /**
     * 나의 번개 (신청/개최) 다건 조회
     * @param authUser
     * @param role
     * @return
     */
    public MyMeetingResponses searchMyMeetings(AuthUser authUser, ParticipantRole role) {
        // 삭제되지 않고 완료처리 되지 않은 미틴에서의 participant 정보만 반환 (빠른 날짜 순서로 정렬)
        List<Participant> participantList = participantRepository.findByUserIdAndRoleExcludingFinished(authUser.getUserId(), role);
        List<MyMeetingResponse> responseList;

        // role = HOST인 경우
        if (role==ParticipantRole.HOST) {
            // meetingList를 DTO로 변환
            responseList = participantList
                    .stream()
                    .map(p -> new MyMeetingResponse(p.getMeeting()))
                    .toList();
            return new MyMeetingResponses(responseList);
        }
        // role = GUEST 인 경우
        responseList = participantList
                .stream()
                .map(p -> new MyMeetingResponse(p.getMeeting(), p.getStatus()))
                .toList();
        return new MyMeetingResponses(responseList);
    }

    /**
     * 번개 통합검색 (급수필터, 정렬타입 적용)
     * @param sortType
     * @param ranks
     * @param page
     * @param size
     * @return
     */
    public Page<MeetingSearchResponse> search(SortType sortType, Ranks ranks, int distance, int page, int size, AuthUser authUser) {
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());
        Pageable pageable = PageRequest.of(page -1, size);
        return meetingQueryDslRepository.search(sortType, ranks, distance, user, pageable);
    }

    /**
     * 완료된 미팅은 완료 표기
     * @param meetingId
     * @return
     */
    @Transactional
    public String finishMyMeeting(AuthUser authUser, Long meetingId) { // auth 인증
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크
        checkAuthority(authUser, meeting);

        // 현재시간이 미팅종료 시간 이후인지 확인
        if (meeting.getEndTime().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.TOO_SOON);
        }

        meeting.markFinished();
        return SuccessMessage.customMessage(SuccessMessage.MEETING_FINISHED);
    }

    private void checkAuthority(AuthUser authUser, Meeting meeting) {
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY, Meeting.class.getSimpleName());
        }
    }

    private void checkTimeValidity(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isAfter(LocalDateTime.now()) || endTime.isBefore((startTime.plusHours(1)))) {
            throw new CustomException(ErrorCode.INVALID_TIME);
        }
    }
}