package com.play.hiclear.domain.timeslot.sevice;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.timeslot.dto.request.TimeSlotRequest;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotResponse;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotSimpleResponse;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.timeslot.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final GymRepository gymRepository;
    private final CourtRepository courtRepository;


    /**
     * 코트 시간대 생성
     *
     * @param authUser        인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param gymId           코트가 등록된 체육관의 ID
     * @param courtNum        시간대를 생성할 코트번호
     * @param timeSlotRequest 생성할 시간대 정보를 포함한 객체
     * @return 생성된 시간대와 체육관, 코트번호를 반환
     */
    @Transactional
    public TimeSlotResponse create(AuthUser authUser, Long gymId, Long courtNum, TimeSlotRequest timeSlotRequest) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 불러오기
        Court court = courtRepository.findByCourtNumAndGymIdOrThrow(courtNum, gymId);

        // 이미 등록된 시간대인지 중복 확인
        Optional<TimeSlot> existTimeSlot = timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), courtNum);
        if (existTimeSlot.isPresent()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_EXIST);
        }

        // 코트 시간대 생성
        TimeSlot timeSlot = new TimeSlot(
                timeSlotRequest.getStartTime(),
                gymId,
                court
        );

        // DB에 저장
        timeSlotRepository.save(timeSlot);

        // DTO객체 반환
        return new TimeSlotResponse(
                timeSlot.getGymId(),
                timeSlot.getCourt().getCourtNum(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime()
        );
    }


    /**
     * 해당 코트의 시간대 목록 조회
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param gymId    코트가 등록된 체육관의 ID
     * @param courtNum 시간대를 조회할 코트번호
     * @return 해당 코트의 시간대 목록을 표시
     */
    public List<TimeSlotSimpleResponse> search(AuthUser authUser, Long gymId, Long courtNum) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 확인
        courtRepository.findByCourtNumAndGymIdOrThrow(courtNum, gymId);

        // 해당 코트 모든 TimeSlot 불러오기
        List<TimeSlot> timeSlotList = timeSlotRepository.findAllByCourt_CourtNum(courtNum);

        return timeSlotList.stream()
                .map(timeSlot -> new TimeSlotSimpleResponse(
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime()))
                .collect(Collectors.toList());
    }


    /**
     * 코트 시간대 삭제(Hard)
     *
     * @param authUser        인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param gymId           코트가 등록된 체육관의 ID
     * @param courtNum        시간대를 삭제할 코트번호
     * @param timeSlotRequest 삭제할 시간대 정보를 포함한 객체
     */
    @Transactional
    public void delete(AuthUser authUser, Long gymId, Long courtNum, TimeSlotRequest timeSlotRequest) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 확인
        courtRepository.findByCourtNumAndGymIdOrThrow(courtNum, gymId);

        // TimeSlot 불러오기
        TimeSlot timeSlot = timeSlotRepository.findByStartTimeAndCourt_CourtNumOrThrow(timeSlotRequest.getStartTime(), courtNum);

        timeSlotRepository.delete(timeSlot);
    }


    // 해당 체육관 사업주가 아닌경우 예외 발생
    private void checkBusinessAuth(Long authUserId, Long gymUserId) {
        if (!Objects.equals(authUserId, gymUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Gym.class.getSimpleName());
        }
    }

}
