package com.play.hiclear.domain.comment.service;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.board.repository.BoardRepository;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.comment.dto.request.CommentCreateRequest;
import com.play.hiclear.domain.comment.dto.request.CommentDeleteRequest;
import com.play.hiclear.domain.comment.dto.request.CommentUpdateRequest;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.notification.service.NotiService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private NotiService notiService;

    private AuthUser authUser;
    private User user;
    private Club club;
    private Board board;
    private Comment comment;
    private Point point;


    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.USER);
        user = new User(authUser.getName(), authUser.getEmail(), "서울 중구 태평로1가 31", "서울 중구 세종대로 110", point,  "encodedPassword", RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(authUser ,"userId", 1L);

        club = new Club(user, "Test Club", 10, "A great club", "서울 중구 태평로1가 31", point,"서울 중구 세종대로 110", "secret");
        ReflectionTestUtils.setField(club, "id", 1L);

        board = new Board("게시글 제목", "게시글 내용", user, club);
        ReflectionTestUtils.setField(board, "id", 1L);

        comment = Comment.builder()
                .content("content")
                .user(user)
                .board(board)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

    }

    @Test
    void create_success() {

        CommentCreateRequest request = new CommentCreateRequest("content");

        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(boardRepository.findByIdAndDeletedAtIsNullOrThrow(board.getId())).thenReturn(board);

        // 알림 서비스 mock 처리
        doNothing().when(notiService).sendNotification(any(), any(), any(), any());  // 알림 서비스의 sendNotification 메서드가 호출되지 않도록 mock

        commentService.create(user.getId(), board.getId(), request);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void update_success() {

        CommentUpdateRequest request = new CommentUpdateRequest("content", "encodedPassword");

        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);

        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // When
        commentService.update(user.getId(), comment.getId(), request);

        verify(commentRepository, Mockito.times(1)).findByIdAndDeletedAtIsNullOrThrow(comment.getId());
    }

    @Test
    void delete_success() {

        CommentDeleteRequest request = new CommentDeleteRequest("encodedPassword");

        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);

        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // When
        commentService.delete(user.getId(), comment.getId(), request);
        assertNotNull(comment.getDeletedAt());
    }
}
