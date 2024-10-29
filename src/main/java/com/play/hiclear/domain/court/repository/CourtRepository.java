package com.play.hiclear.domain.court.repository;

import com.play.hiclear.domain.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long> {
    Optional<Court> findByCourtNumAndGymId(Long courtNum, Long gymId);
    List<Court> findAllByGymId(Long gymId);
}
