package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubUpdateResponse;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private AuthUser authUser;
    private User user;
    private ClubCreateRequest clubCreateRequest;
    private Club club;
    private ClubMember masterMember;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.USER);
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(authUser ,"userId", 1L);

        clubCreateRequest = new ClubCreateRequest("Test Club", 10, "A great club", "Seoul", "encodedPassword");

        club = Club.builder()
                .clubname(clubCreateRequest.getClubname())
                .clubSize(clubCreateRequest.getClubSize())
                .intro(clubCreateRequest.getIntro())
                .region(clubCreateRequest.getRegion())
                .password(clubCreateRequest.getPassword())
                .owner(user)
                .build();
        ReflectionTestUtils.setField(club, "id", 1L);

        masterMember = ClubMember.builder()
                .user(user)
                .club(club)
                .clubMemberRole(ClubMemberRole.ROLE_MASTER)
                .build();
        ReflectionTestUtils.setField(masterMember, "id", 1L);
        ReflectionTestUtils.setField(masterMember, "user", user);
        ReflectionTestUtils.setField(masterMember, "club", club);
        ReflectionTestUtils.setField(masterMember, "clubMemberRole", ClubMemberRole.ROLE_MASTER);
    }

    @Test
    void create_club_success() {
        // Given
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);

        // Mocking the save behavior of the club
        when(clubRepository.save(any(Club.class))).thenAnswer(invocation -> {
            Club savedClub = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedClub, "id", 1L);
            return savedClub;
        });

        // When
        clubService.create(user.getId(), clubCreateRequest);

        // Then
        verify(clubRepository, times(1)).save(any(Club.class));
        verify(clubMemberRepository, times(1)).save(any(ClubMember.class));
    }

    @Test
    void create_fail_user_not_found() {

        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () ->
                clubService.create(authUser.getUserId(), clubCreateRequest)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_success() {

        // given
        ClubUpdateRequest clubUpdateRequest = new ClubUpdateRequest("Test Club", 10, "A great club", "Seoul", "encodedPassword");
        given(userRepository.findByIdAndDeletedAtIsNullOrThrow(anyLong())).willReturn(user);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(anyLong())).thenReturn(club);
        when(clubMemberRepository.findByUserIdAndClubId(anyLong(), anyLong())).thenReturn(Optional.of(masterMember));
        when(passwordEncoder.matches(clubUpdateRequest.getPassword(), club.getPassword())).thenReturn(true);

        // When
        ClubUpdateResponse response = clubService.update(authUser.getUserId(), club.getId(), clubUpdateRequest);

        // Then
        assertNotNull(response);
    }

    @Test
    void delete_success() {

        // given
        ClubDeleteRequest clubDeleteRequest = new ClubDeleteRequest("encodedPassword");
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(anyLong())).thenReturn(user);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(anyLong())).thenReturn(club);
        when(clubMemberRepository.findByUserIdAndClubId(anyLong(), anyLong())).thenReturn(Optional.of(masterMember));
        when(passwordEncoder.matches(clubDeleteRequest.getPassword(), club.getPassword())).thenReturn(true);

        // When
        clubService.delete(authUser.getUserId(), club.getId(), clubDeleteRequest);

        // Then
        assertNotNull(club.getDeletedAt());
    }

}
