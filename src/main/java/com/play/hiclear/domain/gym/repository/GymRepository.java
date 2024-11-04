package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Long>, GymQueryRepository {
    Page<Gym> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Optional<Gym> findByIdAndDeletedAtIsNull(Long gymId);

    Optional<Gym> findByUserAndDeletedAtIsNull(User user);

    default Gym findByIdAndDeletedAtIsNullOrThrow(Long gymId){
        return findByIdAndDeletedAtIsNull(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));
    }

    default Gym findByUserAndDeletedAtIsNullOrThrow(User user){
        return findByUserAndDeletedAtIsNull(user).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));
    }
}
