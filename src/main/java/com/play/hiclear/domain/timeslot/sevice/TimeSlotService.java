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


    @Transactional
    public TimeSlotResponse create(AuthUser authUser, Long gymId, Long courtNum, TimeSlotRequest timeSlotRequest) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 불러오기
        Court court = loadCourt(courtNum, gymId);

        Optional<TimeSlot> existTimeSlot = timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), courtNum);
        if(existTimeSlot.isPresent()){
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


    public List<TimeSlotSimpleResponse> search(AuthUser authUser, Long gymId, Long courtNum) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 불러오기
        Court court = loadCourt(courtNum, gymId);

        // 해당 코트 모든 TimeSlot 불러오기
        List<TimeSlot> timeSlotList = timeSlotRepository.findAllByCourt_CourtNum(courtNum);
        if(timeSlotList.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName());
        }

        return timeSlotList.stream()
                .map(timeSlot -> new TimeSlotSimpleResponse(
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime()))
                .collect(Collectors.toList());
    }


    @Transactional
    public String delete(AuthUser authUser, Long gymId, Long courtNum, TimeSlotRequest timeSlotRequest) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 불러오기
        Court court = loadCourt(courtNum, gymId);

        // TimeSlot 불러오기
        TimeSlot timeSlot = timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), courtNum)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName()));

        timeSlotRepository.delete(timeSlot);

        return "코트번호 " + courtNum + "의 "
                + timeSlotRequest.getStartTime().toString()
                + " ~ " + timeSlotRequest.getStartTime().plusHours(1).toString()
                + "타임이 삭제됐습니다.";

    }


    // 체육관 정보 불러오기
    private Gym loadGym(Long gymId){
        return gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));
    }

    // 해당 체육관 사업주가 아닌경우 예외 발생
    private void checkBusinessAuth(Long authUserId, Long gymUserId){
        if (!Objects.equals(authUserId, gymUserId)){
            throw new CustomException(ErrorCode.NO_AUTHORITY, Gym.class.getSimpleName());
        }
    }

    // 코트 불러오기
    private Court loadCourt(Long courtNum, Long gymId) {
        return courtRepository.findByCourtNumAndGymId(courtNum, gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));
    }

}
