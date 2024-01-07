package blog.controller;

import blog.domain.Category;
import blog.domain.Post;
import blog.dto.PostDetails;
import blog.dto.PostFormDataCreate;
import blog.dto.PostFormDataUpdate;
import blog.dto.PostListItem;
import blog.service.PostService;
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
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private final Post post = new Post();

    private final Category category = new Category();

    @BeforeEach
    void setUp() {
        post.setCreatedAt(LocalDateTime.now());
        post.setCategory(category);
    }

    @Test
    void test_createPost() {
        when(postService.createPost(any())).thenReturn(new PostDetails(post));
        ResponseEntity<PostDetails> responseEntity = postController.createPost(new PostFormDataCreate());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void test_getPostList() {
        List<PostListItem> postListItems = new ArrayList<>();
        when(postService.getPostListItems()).thenReturn(postListItems);
        ResponseEntity<List<PostListItem>> responseEntity = postController.getPostList();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(postListItems, responseEntity.getBody());
    }

    @Test
    void test_getPost() {
        when(postService.getPostDetailsById(anyLong())).thenReturn(new PostDetails(post));
        ResponseEntity<PostDetails> responseEntity = postController.getPost(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void test_getPostListByCategory() {
        List<PostListItem> postListItems = new ArrayList<>();
        when(postService.getPostListByCategory(anyString())).thenReturn(postListItems);
        ResponseEntity<List<PostListItem>> responseEntity = postController.getPostListByCategory("exampleCategory");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(postListItems, responseEntity.getBody());
    }

    @Test
    void test_getPostListByUsername() {
        List<PostListItem> postListItems = new ArrayList<>();
        when(postService.getPostListByUsername(anyString())).thenReturn(postListItems);
        ResponseEntity<List<PostListItem>> responseEntity = postController.getPostListByUsername("exampleUsername");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(postListItems, responseEntity.getBody());
    }

    @Test
    void test_editPost() {
        when(postService.editPost(anyLong(), any())).thenReturn(new PostDetails(post));
        ResponseEntity<PostDetails> responseEntity = postController.editPost(1L, new PostFormDataUpdate());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    void test_deletePost() {
        doNothing().when(postService).deletePost(anyLong());
        ResponseEntity<Void> responseEntity = postController.deletePost(1L);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    void test_restorePost() {
        when(postService.restorePost(anyLong())).thenReturn(new PostDetails(post));
        ResponseEntity<PostDetails> responseEntity = postController.restorePost(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void test_likePost() {
        when(postService.likePost(anyLong())).thenReturn(new PostDetails(post));
        ResponseEntity<PostDetails> responseEntity = postController.likePost(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
