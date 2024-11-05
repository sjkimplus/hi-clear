package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubGetResponse;
import com.play.hiclear.domain.club.dto.response.ClubSearchResponse;
import com.play.hiclear.domain.club.dto.response.ClubUpdateResponse;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     *
     * @param userId    로그인한 유저의 Id
     * @param clubCreateRequest DTO
     */
    @Transactional
    public void create(Long userId, ClubCreateRequest clubCreateRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        //  모임 생성
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

        //  모임 개설자를 모임장으로 클럽멤버 추가
        clubMemberRepository.save(
                ClubMember.builder()
                        .user(user)
                        .club(club)
                        .clubMemberRole(ClubMemberRole.ROLE_MASTER)
                        .build()
        );
    }

    /**
     * 
     * @param clubId    조회하려는 clubId
     * @return          모임 이름, 모임 정원, 모임 소개글, 모임 지역, 회원들
     */
    public ClubGetResponse get(Long clubId) {

        //  모임 유효성 검사
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);

        return new ClubGetResponse(club);
    }

    /**
     *
     * @param userId    현재 로그인된 userId
     * @param clubId    변경할 클럽의 clubId
     * @param clubUpdateRequest 변경할 내용을 담은 DTO
     * @return  모임 이름, 모임 정원, 모임 소개글, 모임 지역
     */
    @Transactional
    public ClubUpdateResponse update(Long userId, Long clubId, ClubUpdateRequest clubUpdateRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
        //  모임 조회
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);
        //  현재 로그인된 유저의 모임멤버 조회
        ClubMember clubMember = clubMemberRepository.findByUserIdAndClubIdOrThrow(user.getId(), club.getId());

        //  모임 권한 확인
        checkClubAdmin(clubMember);
        //  비밀번호 확인
        checkPassword(clubUpdateRequest.getPassword(), club.getPassword());

        club.updateClub(clubUpdateRequest);

        return new ClubUpdateResponse(club);
    }

    /**
     *
     * @return 모임 이름, 모임 소개글
     */
    public Page<ClubSearchResponse> search(int size, int page) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());

        Page<Club> clubList = clubRepository.findAll(pageable);

        return clubList.map(ClubSearchResponse::new);
    }

    @Transactional
    public void delete(Long userId, Long clubId, ClubDeleteRequest clubDeleteRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
        //  모임 조회
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);
        //  모임 멤버 조회
        ClubMember clubMember = clubMemberRepository.findByUserIdAndClubIdOrThrow(user.getId(), club.getId());

        //  모임 권한 확인
        checkClubAdmin(clubMember);
        //  비밀번호 확인
        checkPassword(clubDeleteRequest.getPassword(), club.getPassword());

        club.markDeleted();
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // 모임장 권한 확인
    private void checkClubAdmin(ClubMember clubMember) {
        if (clubMember.getClubMemberRole() != ClubMemberRole.ROLE_MASTER) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Club.class.getSimpleName());
        }
    }
}
