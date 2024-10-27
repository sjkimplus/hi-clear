package com.play.hiclear.domain.schedule.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schduleparticipant.repository.ScheduleParticipantRepository;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    // 모임 일정 생성
    @Transactional
    public ScheduleSearchDetailResponse create(String email, Long clubId, ScheduleRequest scheduleRequestDto) {
        User user = findUserByEmail(email);
        Club club = findClubById(clubId);

        // 클럽 멤버 확인
        validateClubMembership(user, club);

        // 참가자 유효성 검사 및 중복 확인
        Set<Long> participantIds = validateAndGetParticipants(scheduleRequestDto.getParticipants(), club, user.getId());

        // 모임 생성
        Schedule schedule = new Schedule(user, club, scheduleRequestDto.getTitle(), scheduleRequestDto.getDescription(),
                scheduleRequestDto.getRegion(), scheduleRequestDto.getStartTime(), scheduleRequestDto.getEndTime());
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 참가자 목록 추가
        addParticipants(savedSchedule, participantIds, club);

        // 반환 DTO 생성
        return ScheduleSearchDetailResponse.from(savedSchedule, user.getEmail(), user.getId(), List.copyOf(participantIds));
    }

    // 모임 일정 단건 조회
    public ScheduleSearchDetailResponse get(Long scheduleId, String email) {
        // 일정 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Schedule 객체를"));

        // 일정 생성자와 참가자 목록 확인
        User creator = schedule.getUser();
        List<ScheduleParticipant> participants = scheduleParticipantRepository.findBySchedule(schedule);

        boolean isParticipant = participants.stream()
                .anyMatch(participant -> participant.getUser().getEmail().equals(email));

        // 사용자가 일정의 생성자거나 참가자인지 확인
        if (!creator.getEmail().equals(email) && !isParticipant) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN, "이 모임일정을 조회할 권한이 없습니다.");
        }

        // 반환 DTO 생성
        return ScheduleSearchDetailResponse.from(schedule, email, creator.getId(),
                participants.stream().map(participant -> participant.getUser().getId()).toList());
    }

    // 클럽의 모임 일정 목록 조회
    public List<Schedule> search(Long clubId, String email) {
        Club club = findClubById(clubId);
        User user = findUserByEmail(email); // 이 부분은 한 번만 조회

        validateClubMembership(user, club);

        // 클럽의 일정 목록을 조회
        return scheduleRepository.findByClub(club);
    }


    // User 조회
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));
    }

    // Club 조회
    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Club 객체를"));
    }

    // 클럽 멤버 확인 메서드
    private void validateClubMembership(User user, Club club) {
        boolean isMember = club.getClubMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
        if (!isMember) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN, "사용자는 이 클럽의 회원이 아닙니다.");
        }
    }

    // 사용자가 Club의 회원인지 확인하고 유효한 참가자 ID를 반환
    private Set<Long> validateAndGetParticipants(List<Long> participantIds, Club club, Long currentUserId) {
        Set<Long> validParticipantIds = new HashSet<>();
        validParticipantIds.add(currentUserId); // 현재 사용자 ID 추가

        if (participantIds != null) {
            for (Long participantId : participantIds) {
                validateParticipant(participantId, club);
                validParticipantIds.add(participantId);
            }
        }
        return validParticipantIds;
    }

    // 참가자가 클럽의 회원인지 확인
    private void validateParticipant(Long participantId, Club club) {
        User participantUser = userRepository.findById(participantId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        // 클럽의 회원인지 확인
        boolean isMemberOfClub = club.getClubMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(participantUser.getId()));
        if (!isMemberOfClub) {
            throw new CustomException(ErrorCode.SCHEDULE_PARTICIPANT_NOT_FOUND, "사용자는 이 클럽의 회원이 아닙니다.");
        }
    }

    // 생성한 모임일정에 참가자를 추가
    private void addParticipants(Schedule schedule, Set<Long> participantIds, Club club) {
        for (Long participantId : participantIds) {
            User participantUser = userRepository.findById(participantId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

            // 이미 참가자인지 확인
            boolean alreadyParticipating = scheduleParticipantRepository.existsByScheduleAndUser(schedule, participantUser);
            if (alreadyParticipating) {
                throw new CustomException(ErrorCode.SCHEDULE_ALREADY_EXISTS, "사용자는 이미 이 모임에 참가하고 있습니다.");
            }

            ScheduleParticipant additionalParticipant = new ScheduleParticipant(schedule, participantUser, club);
            scheduleParticipantRepository.save(additionalParticipant);
        }
    }

}
