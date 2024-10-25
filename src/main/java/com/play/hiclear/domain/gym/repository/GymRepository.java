package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GymRepository extends JpaRepository<Gym, Long>, GymQueryRepository {

    @Query("SELECT g FROM Gym g WHERE g.user.id = :userId AND g.deletedAt IS NULL")
    Page<Gym> findByUserId(Long userId, Pageable pageabe);
}
