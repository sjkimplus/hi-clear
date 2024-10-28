package com.play.hiclear.domain.participant.repository;

import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMeetingAndUser(Meeting meeting, User user);

    @Query("SELECT COUNT(p) FROM Participant p WHERE p.meeting.id = :meetingId")
    int countParticipantsByMeetingId(@Param("meetingId") Long meetingId);

    @Query("SELECT p.user FROM Participant p WHERE p.meeting = :meeting AND p.status = :status")
    List<Participant> participantsByMeetingAndStatus(@Param("meeting") Meeting meeting, @Param("status") ParticipantStatus status);


}
