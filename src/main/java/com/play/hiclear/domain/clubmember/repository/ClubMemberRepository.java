package com.play.hiclear.domain.clubmember.repository;

import com.play.hiclear.domain.clubmember.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
}
