package com.play.hiclear.domain.participant.repository;

import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMeetingAndUser(Meeting meeting, User user);

    int countByMeetingId(Long meetingId);

    List<Participant> findByMeetingAndStatus(Meeting meeting, ParticipantStatus status);

    List<Participant> findByUserIdAndRole(Long userId, ParticipantRole role);

}
