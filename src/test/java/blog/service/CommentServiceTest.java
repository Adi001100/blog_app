package blog.service;

import blog.domain.Comment;
import blog.domain.CustomUser;
import blog.domain.Post;
import blog.dto.CommentDetails;
import blog.dto.CommentFormDataCreate;
import blog.dto.CommentFormDataUpdate;
import blog.exception.CommentNotFoundByIdException;
import blog.exception.NotTheUsersComment;
import blog.exception.PostNotFoundByIdException;
import blog.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentFormDataCreate commentFormDataCreate;

    @Mock
    private CommentFormDataUpdate commentFormDataUpdate;

    @Mock
    private PostService postService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment = new Comment();
    private Post post = new Post();
    private CustomUser customUser = new CustomUser();
    private Authentication auth = SecurityContextHolder.getContext().getAuthentication();


    @Test
    void test_commentSaveSuccessfulWithoutImg() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentFormDataCreate.getAuthor()).thenReturn("me");
        when(commentFormDataCreate.getCommentBody()).thenReturn("i love my life");
        when(postService.getPostById(commentFormDataCreate.getPostId())).thenReturn(post);
        post.setPublished(true);
        post.setCustomUser(customUser);
        when(commentRepository.save(any())).thenReturn(comment);
        comment.setCreatedAt(LocalDateTime.now());
        CommentDetails commentDetails = commentService.createComment(commentFormDataCreate);
        assertEquals(comment.getAuthor(), commentDetails.getAuthor());
    }

    @Test
    void test_commentSaveFailed() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(postService.getPostById(commentFormDataCreate.getPostId())).thenReturn(null);
        assertThrows(PostNotFoundByIdException.class, () -> commentService.createComment(commentFormDataCreate));
    }

    @Test
    void test_commentDeleteSuccessful() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentService.getLoggedInUser()).thenReturn(applicationUser);
        Long commentId = 1L;
        comment.setId(commentId);
        when(applicationUser.getUsername()).thenReturn("Bob");
        comment.setAuthor(applicationUser.getUsername());
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        commentService.deleteComment(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void test_commentDeleteFailedWrongUser() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentService.getLoggedInUser()).thenReturn(applicationUser);
        Long commentId = 1L;
        comment.setId(commentId);
        comment.setAuthor("bob");
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        assertThrows(NotTheUsersComment.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, times(0)).delete(comment);
    }

    @Test
    void test_postDeleteFailedPostNotFound() {
        Long commentId = 1L;
        comment.setId(commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundByIdException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void test_editCommentSuccessful() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentService.getLoggedInUser()).thenReturn(applicationUser);
        when(postService.getPostById(commentFormDataCreate.getPostId())).thenReturn(post);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(commentFormDataUpdate.getCommentBody()).thenReturn("test");
        when(applicationUser.getUsername()).thenReturn("Bob");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(applicationUser.getUsername());
        CommentDetails commentDetails = commentService.editComment(comment.getId(), commentFormDataUpdate);
        assertEquals(comment.getCommentBody(), commentDetails.getCommentBody());
    }

    @Test
    void test_commentEditFailedWrongUser() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentService.getLoggedInUser()).thenReturn(applicationUser);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor("bob");
        assertThrows(NotTheUsersComment.class, () -> commentService.editComment(comment.getId(), commentFormDataUpdate));
    }

    @Test
    void test_editCommentFailedWitNotFoundCommentById() throws RuntimeException {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundByIdException.class,
                () -> commentService.editComment(comment.getId(), commentFormDataUpdate));
        verify(commentRepository).findById(comment.getId());
    }

    @Test
    void test_editCommentFailedWitNotFoundPostById() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(commentService.getLoggedInUser()).thenReturn(applicationUser);
        when(postService.getPostById(commentFormDataCreate.getPostId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(applicationUser.getUsername()).thenReturn("Bob");
        comment.setAuthor(applicationUser.getUsername());
        assertThrows(PostNotFoundByIdException.class,
                () -> commentService.editComment(comment.getId(), commentFormDataUpdate));
    }

    @Test
    void test_getCommentList() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));
        comment.setCreatedAt(LocalDateTime.now());
        List<CommentDetails> commentList = commentService.getCommentList();
        assertEquals(1, commentList.size());
    }

    @Test
    void test_getCommentDetailsByIdSuccess() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(commentRepository.getById(1L)).thenReturn(comment);
        comment.setCreatedAt(LocalDateTime.now());
        Assertions.assertEquals(comment.getAuthor(), commentService.getCommentDetailsById(1L).getAuthor());
    }

    @Test
    void test_getCommentDetailsByIdFailed() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundByIdException.class, () -> commentService.getCommentDetailsById(1L));
    }

    @Test
    void test_getCommentById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        comment.setAuthor("author");
        comment.setCreatedAt(LocalDateTime.now());
        Assertions.assertEquals(comment.getAuthor(), commentService.getCommentById(1L).getAuthor());
    }
}


