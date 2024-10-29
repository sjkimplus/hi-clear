package com.play.hiclear.domain.clubmember.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.dto.ClubMemberChangeRoleRequest;
import com.play.hiclear.domain.clubmember.dto.request.ClubMemberExpelRequest;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMemberService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(Long userId, Long clubId) {

        User user = findUserById(userId);
        Club club = findClubById(clubId);

        checkClubMember(userId, clubId);

        long memberCount = clubMemberRepository.findAllByClubId(clubId).size();
        if(memberCount + 1 > club.getClubSize()) {
            throw new CustomException(ErrorCode.CLUBMEMBER_OVER);
        }

        clubMemberRepository.save(
                ClubMember.builder()
                        .user(user)
                        .club(club)
                        .clubMemberRole(ClubMemberRole.ROLE_MEMBER)
                        .build()
        );
    }

    @Transactional
    public void withdraw(Long userId, Long clubId) {

        ClubMember member = findClubMemberByUserIdAndClubId(userId, clubId);

        if(member.getClubMemberRole() == ClubMemberRole.ROLE_ADMIN){
            throw new CustomException(ErrorCode.CLUBMEMBER_ADMIN_NOT_WITHDRAW);
        }

        clubMemberRepository.deleteByUserIdAndClubId(userId, clubId);
    }

    @Transactional
    public void expel(Long userId, Long clubId, ClubMemberExpelRequest clubMemberExpelRequest) {

        User user = findUserById(userId);

        ClubMember member = findClubMemberByUserIdAndClubId(userId, clubId);

        if(member.getClubMemberRole() != ClubMemberRole.ROLE_ADMIN &&
                member.getClubMemberRole() != ClubMemberRole.ROLE_MANAGER){
            throw new CustomException(ErrorCode.NO_AUTHORITY, ClubMember.class.getSimpleName());
        }

        checkPassword(clubMemberExpelRequest.getPassword(), user.getPassword());

        User expelUser = userRepository.findByEmail(clubMemberExpelRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, ClubMember.class.getSimpleName()));

        if (user.getId().equals(expelUser.getId())) {
            throw new CustomException(ErrorCode.CLUBMEMBER_NOT_EXPEL_ONESELF);
        }

        clubMemberRepository.deleteByUserIdAndClubId(expelUser.getId(), clubId);
    }

    @Transactional
    public void change(Long userId, Long clubId, ClubMemberChangeRoleRequest clubMemberChangeRoleRequest) {

        // 현재 로그인된 유저의 권한 확인
        ClubMember member = findClubMemberByUserIdAndClubId(userId, clubId);
        if(member.getClubMemberRole() != ClubMemberRole.ROLE_ADMIN){
            throw new CustomException(ErrorCode.NO_AUTHORITY, ClubMember.class.getSimpleName());
        }

        // 비밀번호 확인
        User user = findUserById(userId);
        checkPassword(clubMemberChangeRoleRequest.getPassword(), user.getPassword());

        // 변경할 권한 확인
        if (clubMemberChangeRoleRequest.getRole() == ClubMemberRole.ROLE_ADMIN) {
            throw new CustomException(ErrorCode.CLUBMEMBER_ADMIN_ONLY_ONE);
        }

        // 권한 변경을 할 유저
        User changeUser = userRepository.findByEmail(clubMemberChangeRoleRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, ClubMember.class.getSimpleName()));
        ClubMember changeMember = findClubMemberByUserIdAndClubId(changeUser.getId(), clubId);

        changeMember.change(clubMemberChangeRoleRequest.getRole());
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));
    }

    // Club 조회
    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Club.class.getSimpleName()));
    }

    // ClubMember 조회
    private ClubMember findClubMemberByUserIdAndClubId(Long userId, Long clubId) {
        return clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, ClubMember.class.getSimpleName()));
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // ClubMember 조회
    private void checkClubMember(Long userId, Long clubId) {
        if (clubMemberRepository.existsByUserIdAndClubId(userId, clubId)) {
            throw new CustomException(ErrorCode.CLUBMEMBER_ALREADY_EXISTS);
        }
    }
}
