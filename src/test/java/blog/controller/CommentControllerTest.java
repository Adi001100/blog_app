package blog.controller;

import blog.domain.Comment;
import blog.dto.CommentDetails;
import blog.dto.CommentFormDataCreate;
import blog.dto.CommentFormDataUpdate;
import blog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final Comment comment = new Comment();

    @BeforeEach
    void setUp() {
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void test_createComment() {
        CommentFormDataCreate commentFormDataCreate = new CommentFormDataCreate();
        CommentDetails commentDetails = new CommentDetails(comment);
        when(commentService.createComment(commentFormDataCreate)).thenReturn(commentDetails);
        ResponseEntity<CommentDetails> response = commentController.createComment(commentFormDataCreate);
        verify(commentService, times(1)).createComment(commentFormDataCreate);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_getCommentList() {
        List<CommentDetails> commentList = new ArrayList<>();
        when(commentService.getCommentList()).thenReturn(commentList);
        ResponseEntity<List<CommentDetails>> response = commentController.getCommentList();
        verify(commentService, times(1)).getCommentList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getComment() {
        Long commentId = 1L;
        CommentDetails commentDetails = new CommentDetails(comment);
        when(commentService.getCommentDetailsById(commentId)).thenReturn(commentDetails);
        ResponseEntity<CommentDetails> response = commentController.getComment(commentId);
        verify(commentService, times(1)).getCommentDetailsById(commentId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteComment() {
        Long commentId = 1L;
        ResponseEntity<Void> response = commentController.deleteComment(commentId);
        verify(commentService, times(1)).deleteComment(commentId);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void testEditComment() {
        Long commentId = 1L;
        CommentFormDataUpdate commentFormDataUpdate = new CommentFormDataUpdate();
        CommentDetails commentDetails = new CommentDetails(comment);
        when(commentService.editComment(commentId, commentFormDataUpdate)).thenReturn(commentDetails);
        ResponseEntity<CommentDetails> response = commentController.editComment(commentId, commentFormDataUpdate);
        verify(commentService, times(1)).editComment(commentId, commentFormDataUpdate);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
