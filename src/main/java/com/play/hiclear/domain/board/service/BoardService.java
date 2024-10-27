package com.play.hiclear.domain.board.service;

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
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public BoardCreateResponse create(Long clubId, BoardCreateRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NullPointerException("모임을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NullPointerException("사용자를 찾을수 없습니다."));

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
                board.getCreatedAt(),
                board.getModifiedAt()
        ));
    }

    public BoardSearchDetailResponse get(Long clubId, Long clubboardId) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new NullPointerException("해당 게시글을 찾지 못하였습니다."));

        if(!board.getClub().getId().equals(clubId)) {
            throw new NullPointerException("해당 모임에 속하는 게시글을 찾을 수 없습니다.");
        }

        User user = board.getUser();

        return new BoardSearchDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContext(),
                user.getId(),
                board.getClub().getId()
        );
    }

    public BoardUpdateResponse update(Long clubId, Long clubboardId, BoardUpdateRequest request) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new NullPointerException("해당 게시글을 찾지 못하였습니다."));

        if(!board.getClub().getId().equals(clubId)) {
            throw new NullPointerException("해당 게시글은 이 클럽에 속해있지 않습니다.");
        }

        board.update(
                request.getTitle(),
                request.getContext()
        );

        Board updateBoard = boardRepository.save(board);

        return new BoardUpdateResponse(updateBoard.getId(), updateBoard.getTitle(), updateBoard.getContext());
    }

    public void delete(Long clubId, Long clubboardId) {
        Board board = boardRepository.findById(clubboardId).orElseThrow(()->
                new NullPointerException("해당 게시글을 찾지 못하였습니다."));

        if(!board.getClub().getId().equals(clubId)) {
            throw new NullPointerException("해당 게시글은 이 클럽에 속해있지 않습니다.");
        }

        boardRepository.deleteById(clubboardId);
    }
}
