package com.play.hiclear.domain.court.repository;

import com.play.hiclear.domain.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}
