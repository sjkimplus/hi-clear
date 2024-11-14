//package com.play.hiclear.domain.likes.service;
//
//import com.play.hiclear.common.enums.Ranks;
//import com.play.hiclear.common.exception.CustomException;
//import com.play.hiclear.common.exception.ErrorCode;
//import com.play.hiclear.domain.auth.entity.AuthUser;
//import com.play.hiclear.domain.board.entity.Board;
//import com.play.hiclear.domain.comment.entity.Comment;
//import com.play.hiclear.domain.comment.repository.CommentRepository;
//import com.play.hiclear.domain.likes.entity.Likes;
//import com.play.hiclear.domain.likes.repository.LikesRepository;
//import com.play.hiclear.domain.user.entity.User;
//import com.play.hiclear.domain.user.enums.UserRole;
//import com.play.hiclear.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class LikesServiceTest {
//
//    @Mock
//    private LikesRepository likesRepository;
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private LikesService likesService;
//
//    private User user;
//    private AuthUser authUser;
//    private Comment comment;
//
//    @BeforeEach
//    void setUp(){
//        user = new User("실험체", "test@gmail.com", "encoded_password", "SEOUL", Ranks.RANK_A, UserRole.USER);
//        comment = Comment.builder()
//                .id(1L)
//                .user(user)
//                .board(new Board())
//                .content("comment")
//                .threads(new ArrayList<>())
//                .build();
//        authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
//    }
//
//    @Test //댓글, 사용자가 정상적으로 존재 / 댓글의 좋아요가 없을시 새로운 좋아요 생성
//    public void ToggleLike_addSucces(){
//        Likes existingLike = new Likes(user, comment);
//        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
//        when(userRepository.findById(authUser.getUserId())).thenReturn(Optional.of(user));
//        when(likesRepository.findByCommentIdAndUserId(comment.getId(), user.getId())).thenReturn(Optional.of(existingLike));
//
//        likesService.toggleLike(comment.getId(), authUser);
//
//        verify(likesRepository).save(existingLike);
//    }
//
//    @Test //이미 생성된 좋아요의 토글 기능
//    public void ToggleLike_toggleSucces(){
//        Likes existingLike = new Likes(user, comment);
//        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
//        when(userRepository.findById(authUser.getUserId())).thenReturn(Optional.of(user));
//        when(likesRepository.findByCommentIdAndUserId(comment.getId(), user.getId())).thenReturn(Optional.of(existingLike));
//
//        likesService.toggleLike(comment.getId(), authUser);
//
//        verify(likesRepository).save(existingLike);
//    }
//
//    @Test
//    public void ToggleLike_fail_user_not_found(){
//        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
//        when(userRepository.findById(authUser.getUserId())).thenReturn(Optional.empty());
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            likesService.toggleLike(comment.getId(), authUser);
//        });
//
//        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    public void ToggleLike_fail_comment_not_found(){
//        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            likesService.toggleLike(comment.getId(), authUser);
//        });
//
//        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    public void GetLikes_succes() {
//        when(likesRepository.countByCommentId(comment.getId())).thenReturn(5L);
//
//        Long likesCount = likesService.get(comment.getId());
//
//        assertEquals(5L, likesCount); // 5개
//    }
//}