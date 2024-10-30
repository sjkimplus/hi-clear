package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.gym.dto.request.GymCreateRequest;
import com.play.hiclear.domain.gym.dto.request.GymUpdateRequest;
import com.play.hiclear.domain.gym.dto.response.GymCreateResponse;
import com.play.hiclear.domain.gym.dto.response.GymDetailResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.dto.response.GymUpdateResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {
    private final UserRepository userRepository;
    private final GymRepository gymRepository;


    @Transactional
    public GymCreateResponse create(AuthUser authUser, GymCreateRequest request) {

        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

        Gym gym = new Gym(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                GymType.of(request.getGymType()),
                user
        );

        gymRepository.save(gym);

        return new GymCreateResponse(
                gym.getId(),
                gym.getName(),
                gym.getDescription(),
                gym.getAddress(),
                gym.getGymType()
        );
    }


    public Page<GymSimpleResponse> search(int page, int size, String name, String address, GymType gymType) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.searchGyms(name, address, gymType, pageable);


        return gyms.map(this::convertGymSimpleResponse);
    }


    public Page<GymSimpleResponse> businessSearch(AuthUser authUser, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.findByUserIdAndDeletedAtIsNull(authUser.getUserId(), pageable);

        // 해당 계정으로 등록된 체육관이 없는경우
        if(gyms.getTotalElements() == 0){
            throw new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName());
        }

        return gyms.map(this::convertGymSimpleResponse);
    }


    @Transactional
    public GymUpdateResponse update(AuthUser authUser, Long gymId, GymUpdateRequest gymUpdateRequest) {

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));

        // 해당 체육관 사업주가 아닌경우 예외 발생
        if (!Objects.equals(gym.getUser().getId(), authUser.getUserId())){
            throw new CustomException(ErrorCode.NO_AUTHORITY, Gym.class.getSimpleName());
        }

        gym.update(
                gymUpdateRequest.getUpdateName(),
                gymUpdateRequest.getUpdateDescription(),
                gymUpdateRequest.getUpdateAddress()
        );

        return new GymUpdateResponse(
                gym.getName(),
                gym.getDescription(),
                gym.getAddress()
        );
    }

    @Transactional
    public void delete(AuthUser authUser, Long gymId) {

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));

        // 해당 체육관 사업주가 아닌경우 예외 발생
        if (!Objects.equals(gym.getUser().getId(), authUser.getUserId())){
            throw new CustomException(ErrorCode.NO_AUTHORITY); // NO_AUTHORITY도 메세지를 입력받아범용적으로 사용하도록 제안
        }

        gym.markDeleted();

    }

    // GymSimpleResponse 객체 변환 메서드
    private GymSimpleResponse convertGymSimpleResponse(Gym gym){
        return new GymSimpleResponse(
                gym.getName(),
                gym.getAddress());
    }

    public GymDetailResponse get(Long gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));

        if (gym.getGymType()==GymType.PRIVATE) {
            return new GymDetailResponse(gym);
        } else { // public 인 경우 모임 리스트와 당날 스케줄 정보 가져오기
            // 공립 체육관의 모임 리스트 가져오기


            // 당날 스케줄 정보가져오기

            return new GymDetailResponse(gym);
        }
    }
}
