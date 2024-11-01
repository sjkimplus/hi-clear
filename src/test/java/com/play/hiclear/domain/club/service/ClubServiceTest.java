package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static com.play.hiclear.common.enums.Ranks.RANK_B;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    @InjectMocks
    private ClubService clubService;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubMemberRepository clubMemberRepository;

    private AuthUser authUser;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.USER);
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", Ranks.RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    void create_club_success() {

        // Given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ClubCreateRequest request = new ClubCreateRequest("testName", 5, "testIntro", "testRegion", "testPassword");

        // when
        clubService.create(user.getId(), request);

        verify(clubRepository, times(1)).save((any(Club.class)));
        verify(clubMemberRepository, times(1)).save((any(ClubMember.class)));
    }
}
