package com.play.hiclear.domain.court.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long> {
    Optional<Court> findByCourtNumAndGymId(Long courtNum, Long gymId);

    List<Court> findAllByGymId(Long gymId);

    Optional<Court> findByIdAndDeletedAtIsNull(Long courtId);

    default Court findByCourtNumAndGymIdOrThrow(Long courtNum, Long gymId) {
        return findByCourtNumAndGymId(courtNum, gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));
    }

    default Court findByIdAndDeletedAtIsNullOrThrow(Long courtId) {
        return findByIdAndDeletedAtIsNull(courtId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));
    }
}
