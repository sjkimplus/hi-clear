package com.play.hiclear.domain.clubmember.repository;

import com.play.hiclear.domain.clubmember.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    Optional<ClubMember> findByUserIdAndClubId(Long userId, Long clubId);

    boolean existsByUserIdAndClubId(Long userId, Long clubsId);

    void deleteByUserIdAndClubId(Long userId, Long clubsId);

    List<ClubMember> findAllByClubId(Long clubsId);
}
