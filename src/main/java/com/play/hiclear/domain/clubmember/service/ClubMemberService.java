package com.play.hiclear.domain.clubmember.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.dto.request.ClubMemberChangeRoleRequest;
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

    /**
     * 
     * @param userId    가입하려는 userId
     * @param clubId    가입하려는 clubId
     */
    @Transactional
    public void join(Long userId, Long clubId) {

        //  유저 조회
        User user = findUserById(userId);
        //  모임 조회
        Club club = findClubById(clubId);

        // 모임 가입 여부 확인
        checkClubMember(userId, clubId);

        //  모임 정원 확인 로직
        long memberCount = clubMemberRepository.findAllByClubId(clubId).size();
        if (memberCount >= club.getClubSize()) {
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

    /**
     *
     * @param userId    모임 탈퇴할 userId
     * @param clubId    모임 탈퇴할 clubId
     */
    @Transactional
    public void withdraw(Long userId, Long clubId) {

        //  유저 조회
        User user = findUserById(userId);
        //  모임 조회
        Club club = findClubById(clubId);
        //  모임 멤버 조회
        ClubMember member = findClubMemberByUserIdAndClubId(user.getId(), club.getId());

        //  모임장 확인
        if(member.getClubMemberRole() == ClubMemberRole.ROLE_MASTER){
            throw new CustomException(ErrorCode.CLUBMEMBER_ADMIN_NOT_WITHDRAW);
        }

        clubMemberRepository.deleteByUserIdAndClubId(user.getId(), club.getId());
    }

    /**
     *
     * @param userId    로그인 된 userId
     * @param clubId    clubId
     * @param clubMemberExpelRequest    탈퇴시킬 member의 email과 비밀번호를 담은 DTO
     */
    @Transactional
    public void expel(Long userId, Long clubId, ClubMemberExpelRequest clubMemberExpelRequest) {

        // 유저 조회
        User user = findUserById(userId);
        // 모임 조회
        Club club = findClubById(clubId);

        //  모임 멤버 조회
        ClubMember member = findClubMemberByUserIdAndClubId(user.getId(), club.getId());

        // 추방할 권한 유무 확인
        if(member.getClubMemberRole() != ClubMemberRole.ROLE_MASTER &&
                member.getClubMemberRole() != ClubMemberRole.ROLE_MANAGER){
            throw new CustomException(ErrorCode.NO_AUTHORITY, ClubMember.class.getSimpleName());
        }

        //  비밀번호 확인
        checkPassword(clubMemberExpelRequest.getPassword(), user.getPassword());

        // 추방할 유저 조회
        User expelUser = userRepository.findByEmail(clubMemberExpelRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, ClubMember.class.getSimpleName()));

        if (user.getId().equals(expelUser.getId())) {
            throw new CustomException(ErrorCode.CLUBMEMBER_NOT_EXPEL_ONESELF);
        }

        clubMemberRepository.deleteByUserIdAndClubId(expelUser.getId(), club.getId());
    }

    /**
     *
     * @param userId    로그인 된 userId
     * @param clubId    clubId
     * @param clubMemberChangeRoleRequest   권한 변경할 member의 email과 비밀번호를 담은 DTO
     */
    @Transactional
    public void change(Long userId, Long clubId, ClubMemberChangeRoleRequest clubMemberChangeRoleRequest) {

        // 유저 조회
        User user = findUserById(userId);
        // 모임 조회
        Club club = findClubById(clubId);

        // 현재 로그인된 유저의 모임 멤버 권한 확인
        ClubMember member = findClubMemberByUserIdAndClubId(user.getId(), club.getId());
        if(member.getClubMemberRole() != ClubMemberRole.ROLE_MASTER){
            throw new CustomException(ErrorCode.NO_AUTHORITY, ClubMember.class.getSimpleName());
        }

        // 비밀번호 확인
        checkPassword(clubMemberChangeRoleRequest.getPassword(), user.getPassword());

        // 변경할 권한 확인
        if (clubMemberChangeRoleRequest.getRole() == ClubMemberRole.ROLE_MASTER) {
            throw new CustomException(ErrorCode.CLUBMEMBER_ADMIN_ONLY_ONE);
        }

        // 권한 변경을 할 유저
        User changeUser = userRepository.findByEmail(clubMemberChangeRoleRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, ClubMember.class.getSimpleName()));
        ClubMember changeMember = findClubMemberByUserIdAndClubId(changeUser.getId(), club.getId());

        changeMember.change(clubMemberChangeRoleRequest.getRole());
    }

    // User 조회
    private User findUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
    }

    // 모임 조회
    private Club findClubById(Long clubId) {
        return clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);
    }

    // 모임 멤버 조회
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

    // 모임 멤버 유효성 검사
    private void checkClubMember(Long userId, Long clubId) {
        if (clubMemberRepository.existsByUserIdAndClubId(userId, clubId)) {
            throw new CustomException(ErrorCode.CLUBMEMBER_ALREADY_EXISTS);
        }
    }
}
