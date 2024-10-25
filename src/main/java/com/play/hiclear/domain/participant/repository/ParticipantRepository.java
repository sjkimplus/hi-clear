package com.play.hiclear.domain.participant.repository;

import com.play.hiclear.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
