package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.gym.dto.request.GymSaveRequest;
import com.play.hiclear.domain.gym.dto.response.GymSaveResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {
    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    @Transactional
    public GymSaveResponse createGym(AuthUser authUser, GymSaveRequest request) {

        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        Gym gym = new Gym(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                GymType.of(request.getGymType()),
                user
        );

        gymRepository.save(gym);

        return new GymSaveResponse(
                gym.getId(),
                gym.getName(),
                gym.getDescription(),
                gym.getAddress(),
                gym.getGymType()
        );
    }

    public Page<GymSimpleResponse> searchGyms(int page, int size, String name, String address, GymType gymType) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.searchGyms(name, address, gymType, pageable);


        return gyms.map(this::convertGymSimpleResponse);
    }

    private GymSimpleResponse convertGymSimpleResponse(Gym gym){
        return new GymSimpleResponse(
                gym.getName(),
                gym.getAddress());
    }
}
