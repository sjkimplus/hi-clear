package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GymRepository extends JpaRepository<Gym, Long>, GymQueryRepository {
    Page<Gym> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
}
