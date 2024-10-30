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
import com.play.hiclear.domain.meeting.entity.Meeting;
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

    @Transactional
    public BoardCreateResponse create(Long clubId, BoardCreateRequest request, AuthUser authUser) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Club.class.getSimpleName()));

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

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

    public BoardSearchDetailResponse get(Long clubId, Long clubboardId) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));

        if(!board.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName());
        }

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

    @Transactional
    public BoardUpdateResponse update(Long clubId, Long clubboardId, BoardUpdateRequest request, AuthUser authUser) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));

        if(!board.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName());
        }

        if (!board.getUser().getId().equals(authUser.getUserId())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Board.class.getSimpleName());
        }

        board.update(
                request.getTitle(),
                request.getContext()
        );


        return new BoardUpdateResponse(board.getId(), board.getTitle(), board.getContext());
    }

    @Transactional
    public void delete(Long clubId, Long clubboardId, AuthUser authUser) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));

        if(!board.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName());
        }

        if (!board.getUser().getId().equals(authUser.getUserId())) {  // authUser.getUserId() 사용
            throw new CustomException(ErrorCode.NO_AUTHORITY, Board.class.getSimpleName());
        }

        boardRepository.deleteById(clubboardId);
    }
}
