package com.play.hiclear.domain.court.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.dto.request.CourtCreateRequest;
import com.play.hiclear.domain.court.dto.response.CourtCreateResponse;
import com.play.hiclear.domain.court.dto.response.CourtSearchResponse;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtService {

    private final GymRepository gymRepository;
    private final CourtRepository courtRepository;

    @Transactional
    public CourtCreateResponse create(AuthUser authUser, Long gymId, CourtCreateRequest courtCreateRequest) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId(), Court.class.getSimpleName());

        // 코트 번호 자동지정
        Long courtNum = 1L;
        if (!gym.getCourts().isEmpty()) {
            List<Court> courtList = gym.getCourts();
            courtList.sort(Comparator.comparing(Court::getCourtNum));
            for (Court court : courtList) {
                if (Objects.equals(court.getCourtNum(), courtNum)) {
                    courtNum++;
                } else {
                    break;
                }
            }
        }

        Court court = new Court(
                courtNum,
                courtCreateRequest.getPrice(),
                gym
        );

        courtRepository.save(court);

        return new CourtCreateResponse(
                court.getCourtNum(),
                court.getPrice()
        );
    }


    public List<CourtSearchResponse> search(AuthUser authUser, Long gymId) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        List<Court> courtList = courtRepository.findAllByGymId(gymId);
        List<CourtSearchResponse> courtSearchResponseList = new ArrayList<>();

        for (Court court : courtList) {
            courtSearchResponseList.add(new CourtSearchResponse(
                    court.getCourtNum(),
                    court.getPrice(),
                    court.getCourtStatus()
            ));
        }
        courtSearchResponseList.sort(Comparator.comparing(CourtSearchResponse::getCourtNum));

        return courtSearchResponseList;
    }


    @Transactional
    public CourtCreateResponse update(AuthUser authUser, Long gymId, Long courtNum, CourtCreateRequest courtCreateRequest) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId(), Court.class.getSimpleName());

        // 코트 불러오기
        Court court = loadCourt(courtNum, gymId);

        court.update(
                courtCreateRequest.getPrice()
        );

        return new CourtCreateResponse(
                court.getCourtNum(),
                court.getPrice()
        );
    }


    @Transactional
    public void delete(AuthUser authUser, Long gymId, Long courtNum) {

        // 체육관 정보 불러오기
        Gym gym = loadGym(gymId);

        // 체육관 소유 사업자 확인
        checkBusinessAuth(authUser.getUserId(), gym.getUser().getId(), Court.class.getSimpleName());

        Court court = loadCourt(courtNum, gymId);

        courtRepository.delete(court);
    }



    // 체육관 불러오기
    private Gym loadGym(Long gymId) {
        return gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));
    }


    // 체육관 소유 사업자 확인
    private void checkBusinessAuth(Long userId, Long gymUserId, String message) {
        if (!userId.equals(gymUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, message);
        }
    }

    // 코트 불러오기
    private Court loadCourt(Long courtNum, Long gymId){
        return courtRepository.findByCourtNumAndGymId(courtNum, gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));
    }

}
