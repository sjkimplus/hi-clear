package com.play.hiclear.domain.board.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.board.dto.request.BoardCreateRequest;
import com.play.hiclear.domain.board.dto.request.BoardUpdateRequest;
import com.play.hiclear.domain.board.dto.response.BoardSearchDetailResponse;
import com.play.hiclear.domain.board.dto.response.BoardSearchResponse;
import com.play.hiclear.domain.board.dto.response.BoardCreateResponse;
import com.play.hiclear.domain.board.dto.response.BoardUpdateResponse;
import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.board.repository.BoardRepository;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 생성
     *
     * @param clubId
     * @param request
     * @param authUser
     * @return BoardCreateResponse
     */
    @Transactional
    public BoardCreateResponse create(Long clubId, BoardCreateRequest request, AuthUser authUser) {

        // 모임 조회
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);

        // 유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Board board = new Board(
                request.getTitle(),
                request.getContext(),
                user,
                club
        );
        Board saveBoard = boardRepository.save(board);

        return new BoardCreateResponse(
                saveBoard.getId(),
                saveBoard.getTitle(),
                saveBoard.getContext(),
                saveBoard.getUser().getId(),
                saveBoard.getClub().getId()
        );
    }

    /**
     * 게시글 다건 조회
     *
     * @param clubId
     * @param page
     * @param size
     * @return Page<BoardSearchResponse>
     */
    public Page<BoardSearchResponse> search(Long clubId, int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        Page<Board> boards = boardRepository.findByClubId(clubId, pageable);

        return boards.map(board -> new BoardSearchResponse(
                board.getId(),
                board.getTitle(),
                board.getContext(),
                board.getUser().getId(),
                board.getUser().getName(),
                board.getUser().getEmail(),
                board.getCreatedAt(),
                board.getModifiedAt()
        ));
    }

    /**
     * 게시글 단건 조회
     *
     * @param clubId
     * @param clubboardId
     * @return BoardSearchDetailResponse
     */
    public BoardSearchDetailResponse get(Long clubId, Long clubboardId) {

        // 게시글 조회
        Board board = boardRepository.findBoardIdOrThrow(clubboardId);

        // Club 확인
        checkClub(clubId, board);

        User user = board.getUser();

        return new BoardSearchDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContext(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                board.getClub().getId()
        );
    }

    /**
     * 게시글 수정
     *
     * @param clubId
     * @param clubboardId
     * @param request
     * @param authUser
     * @return BoardUpdateResponse
     */
    @Transactional
    public BoardUpdateResponse update(Long clubId, Long clubboardId, BoardUpdateRequest request, AuthUser authUser) {

        // 게시글 조회
        Board board = boardRepository.findBoardIdOrThrow(clubboardId);

        // Club 확인
        checkClub(clubId, board);

        // User 확인
        checkUser(board, authUser);

        board.update(
                request.getTitle(),
                request.getContext()
        );


        return new BoardUpdateResponse(board.getId(), board.getTitle(), board.getContext());
    }

    /**
     * 게시글 삭제(soft로 변경필요)
     *
     * @param clubId
     * @param clubboardId
     * @param authUser
     */
    @Transactional
    public void delete(Long clubId, Long clubboardId, AuthUser authUser) {

        // 게시글 조회
        Board board = boardRepository.findBoardIdOrThrow(clubboardId);

        // Club 확인
        checkClub(clubId, board);

        // User 확인
        checkUser(board, authUser);

        boardRepository.deleteById(clubboardId);
    }

    // Club 확인
    private void checkClub(Long clubId, Board board) {
        if(!board.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, Club.class.getSimpleName());
        }
    }

    // User 확인
    private void checkUser(Board board, AuthUser authUser) {
        if (!board.getUser().getId().equals(authUser.getUserId())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, User.class.getSimpleName());
        }
    }

}
