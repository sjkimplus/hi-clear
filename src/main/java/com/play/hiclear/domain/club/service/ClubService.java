package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.dto.*;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(Long userId, ClubCreateRequest clubCreateRequest) {

        User user = findUserById(userId);

        Club club = clubRepository.save(
                Club.builder()
                .clubname(clubCreateRequest.getClubname())
                .clubSize(clubCreateRequest.getClubSize())
                .intro(clubCreateRequest.getIntro())
                .region(clubCreateRequest.getRegion())
                .password(passwordEncoder.encode(clubCreateRequest.getPassword()))
                .owner(user)
                .build()
        );

        clubMemberRepository.save(
                ClubMember.builder()
                        .user(user)
                        .club(club)
                        .clubMemberRole(ClubMemberRole.ROLE_ADMIN)
                        .build()
        );
    }

    public ClubGetResponse get(Long clubId) {

        Club club = findClubById(clubId);

        return new ClubGetResponse(club);
    }

    @Transactional
    public ClubUpdateResponse update(Long userId, Long clubsId, ClubUpdateRequest clubUpdateRequest) {

        Club club = findClubById(clubsId);
        ClubMember clubMember = findClubMemberByUserIdAndClubId(userId, clubsId);

        checkClubAdmin(clubMember);
        checkPassword(clubUpdateRequest.getPassword(), club.getPassword());

        club.updateClub(clubUpdateRequest);

        return new ClubUpdateResponse(club);
    }

    public List<ClubSearchResponse> search() {

        List<Club> clubList = clubRepository.findAll();

        return clubList.stream().map(ClubSearchResponse::new).toList();
    }

    @Transactional
    public void delete(Long userId, Long clubsId, ClubDeleteRequest clubDeleteRequest) {

        Club club = findClubById(clubsId);
        ClubMember clubMember = findClubMemberByUserIdAndClubId(userId, clubsId);

        checkClubAdmin(clubMember);
        checkPassword(clubDeleteRequest.getPassword(), club.getPassword());

        club.markDeleted();
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저를"));
    }

    // Club 조회
    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 모임을"));
    }

    // ClubMember 조회
    private ClubMember findClubMemberByUserIdAndClubId(Long userId, Long clubId) {
        return clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 멤버를"));
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // 모임장 권한 확인
    private void checkClubAdmin(ClubMember clubMember) {
        if (clubMember.getClubMemberRole() != ClubMemberRole.ROLE_ADMIN) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "해당 기능");
        }
    }
}
