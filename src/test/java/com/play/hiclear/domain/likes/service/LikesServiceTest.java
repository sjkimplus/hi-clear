//package com.play.hiclear.domain.likes.service;
//
//import com.play.hiclear.common.exception.CustomException;
//import com.play.hiclear.common.exception.ErrorCode;
//import com.play.hiclear.domain.auth.entity.AuthUser;
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
//import org.springframework.test.util.ReflectionTestUtils;
//
//import static com.play.hiclear.common.enums.Ranks.RANK_A;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
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
//    private AuthUser authUser1;
//    private AuthUser authUser2;
//    private User user1;
//    private User user2;
//    private Comment comment;
//    private Likes likes;
//
//    @BeforeEach
//    void setUp(){
//
//        authUser1 = new AuthUser(1L, "테스트1", "test1@gmail.com", UserRole.USER);
//        user1 = new User(authUser1.getName(), authUser1.getEmail(),"서울 관악구 신림동 533-29",
//                "서울 관악구 조원로 89-1", null, "encodedPassword", RANK_A, UserRole.USER);
//        ReflectionTestUtils.setField(user1, "id", 1L);
//        ReflectionTestUtils.setField(authUser1, "userId", 1L);
//
//
////        authUser2 = new AuthUser(2L, "테스트2", "test2@gmail.com", UserRole.USER);
////        user2 = new User(authUser2.getName(), authUser2.getEmail(),"서울 관악구 신림동 533-29",
////                "서울 관악구 조원로 89-1", null, "encodedPassword", RANK_A, UserRole.USER);
////        ReflectionTestUtils.setField(user2, "id", 2L);
////        ReflectionTestUtils.setField(authUser2, "userId", 2L);
//
//        comment = new Comment();
//        ReflectionTestUtils.setField(comment, "id", 1L);
//
//        likes = new Likes(user1, comment);
//    }
//
//    @Test //댓글, 사용자가 정상적으로 존재 / 댓글의 좋아요가 없을시 새로운 좋아요 생성
//    public void ToggleLike_addSucces(){
//        // when
//        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);
//        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user1.getId())).thenReturn(user1);
//        when(likesRepository.findByCommentIdAndUserId(comment.getId(), user1.getId())).thenReturn(null);
//
//        // then
//        likesService.toggleLike(comment.getId(), authUser1);
//
//        verify(likesRepository, times(1)).save(any(Likes.class));
//    }
//
//    @Test //이미 생성된 좋아요의 토글 기능
//    public void ToggleLike_removeSucces(){
//        // when
//        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);
//        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user1.getId())).thenReturn(user1);
//        when(likesRepository.findByCommentIdAndUserId(comment.getId(), user1.getId())).thenReturn(likes);
//
//        // then
//        likesService.toggleLike(comment.getId(), authUser1);
//
//        verify(likesRepository, times(1)).save(any(Likes.class));
//    }
//
//    @Test
//    public void ToggleLike_fail_user_not_found(){
//        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);
//        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user1.getId()))
//                .thenThrow(new CustomException(ErrorCode.NOT_FOUND));
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            likesService.toggleLike(comment.getId(), authUser1);
//        });
//        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    public void ToggleLike_fail_comment_not_found(){
//        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId()))
//                .thenThrow(new CustomException(ErrorCode.NOT_FOUND));
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            likesService.toggleLike(comment.getId(), authUser1);
//        });
//
//        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    public void getLikeCount() {
//        // given
//        long expectedCount = 5L;
//
//        // when
//        when(commentRepository.findByIdAndDeletedAtIsNullOrThrow(comment.getId())).thenReturn(comment);
//        when(likesRepository.countByCommentId(comment.getId())).thenReturn(expectedCount);
//
//        // then
//        Long result = likesService.get(comment.getId());
//        assertEquals(expectedCount, result);
//    }
//}