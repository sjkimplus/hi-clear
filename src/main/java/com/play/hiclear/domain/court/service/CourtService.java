package com.play.hiclear.domain.court.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.dto.request.CourtCreateRequest;
import com.play.hiclear.domain.court.dto.request.CourtUpdateRequest;
import com.play.hiclear.domain.court.dto.response.CourtCreateResponse;
import com.play.hiclear.domain.court.dto.response.CourtSearchResponse;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtService {

    private final GymRepository gymRepository;
    private final CourtRepository courtRepository;


    /**
     * 코트 등록
     * @param authUser
     * @param gymId
     * @param courtCreateRequest
     * @return CourtCreateResponse
     */
    @Transactional
    public CourtCreateResponse create(AuthUser authUser, Long gymId, CourtCreateRequest courtCreateRequest) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        List<Court> courtList = gym.getCourts();
        // courtList에 이미 존재하는 courtNum이 있는지 확인
        boolean exists = courtList.stream()
                .anyMatch(court -> court.getCourtNum() == courtCreateRequest.getCourtNum());
        if (exists) {
            throw new CustomException(ErrorCode.COURT_ALREADY_EXIST);
        }

        Court court = new Court(
                courtCreateRequest.getCourtNum(),
                courtCreateRequest.getPrice(),
                gym
        );

        courtRepository.save(court);

        return new CourtCreateResponse(
                court.getCourtNum(),
                court.getPrice()
        );
    }


    /**
     * 해당 체육관 코트 불러오기
     * @param authUser
     * @param gymId
     * @return List<CourtSearchResponse>
     */
    public List<CourtSearchResponse> search(AuthUser authUser, Long gymId) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관의 코트 불러오기
        List<Court> courtList = courtRepository.findAllByGymId(gym.getId());

        // 불러온 코트 정보 DTO로 반환
        return courtList.stream()
                .map(c -> new CourtSearchResponse(
                        c.getCourtNum(),
                        c.getPrice()
                )).collect(Collectors.toList());
    }


    /**
     * 코트 정보 수정(가격 변경)
     * @param authUser
     * @param gymId
     * @param courtNum
     * @param courtUpdateRequest
     * @return CourtCreateResponse
     */
    @Transactional
    public CourtCreateResponse update(AuthUser authUser, Long gymId, Long courtNum, CourtUpdateRequest courtUpdateRequest) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 불러오기
        Court court = courtRepository.findByCourtNumAndGymIdOrThrow(courtNum, gym.getId());

        // 코트 정보 수정
        court.update(
                courtUpdateRequest.getPrice()
        );

        // DTO 객체 반환
        return new CourtCreateResponse(
                court.getCourtNum(),
                court.getPrice()
        );
    }


    /**
     * 코트 삭제(Soft)
     * @param authUser
     * @param gymId
     * @param courtNum
     */
    @Transactional
    public void delete(AuthUser authUser, Long gymId, Long courtNum) {

        // 체육관 정보 불러오기
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId());

        // 코트 정보 불러오기
        Court court = courtRepository.findByCourtNumAndGymIdOrThrow(courtNum, gym.getId());

        // 코트 삭제
        court.markDeleted();
    }

    // 체육관 소유 사업자 확인
    private void checkBusinessAuth(Long userId, Long gymUserId) {
        if (!userId.equals(gymUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Gym.class.getSimpleName());
        }
    }

}
