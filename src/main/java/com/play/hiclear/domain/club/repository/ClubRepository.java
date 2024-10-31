package com.play.hiclear.domain.club.repository;

import com.play.hiclear.domain.club.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByIdAndDeletedAtIsNull(Long clubId);
    Page<Club> findAll(Pageable pageable);
}
