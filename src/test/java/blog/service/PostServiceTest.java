package blog.service;

import blog.domain.Category;
import blog.domain.Comment;
import blog.domain.CustomUser;
import blog.domain.Post;
import blog.dto.PostDetails;
import blog.dto.PostFormDataCreate;
import blog.dto.PostFormDataUpdate;
import blog.dto.PostListItem;
import blog.exception.*;
import blog.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFormDataCreate postFormDataCreate;

    @Mock
    private PostFormDataUpdate postFormDataUpdate;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private EmailService emailService;

    @Mock
    private ChatGptService chatGptService;

    @Mock
    private CustomUserService customUserService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Comment comment;

    @Mock
    private CustomUser customUser;

    @InjectMocks
    private PostService postService;

    private Post post;

    private Post timedPost;

    private Category category;

    private final String IMG_URL_TO_UPLOAD = "src/test/resources/dock.jpeg";

    private final String IMG_URL_FROM_CLOUDINARY = "https://res.cloudinary.com/drknstmtp/image/upload/v1695896173/dock.jpg";

    private final String VIDEO_URL_TO_UPLOAD = "src/test/resources/video.mp4";

    private final String VIDEO_URL_FROM_CLOUDINARY = "https://res.cloudinary.com/drknstmtp/video/upload/v1695896180/video.mp4";


    @BeforeEach
    void init() {
        category = new Category();
        category.setId(1L);
        category.setCategoryName("boat");
        post = new Post();
        post.setId(1L);
        post.setTitle("power boat");
        post.setPostBody("the real life is life on the water");
        post.setComments(new ArrayList<>());
        post.setCategory(category);
        post.setCustomUser(new CustomUser());
        post.getCustomUser().setUsername("bob");
        post.setNumberOfLike(0);
        timedPost = new Post();
        timedPost.setId(2L);
        timedPost.setTitle("testelek");
        timedPost.setPostBody("creative thinking");
        timedPost.setPublishTime(LocalDateTime.now().plusHours(1));
        timedPost.setPublished(false);
        timedPost.setComments(new ArrayList<>());
        timedPost.setCategory(category);
    }

    private final List<Comment> commentList = new ArrayList<>();

    @Test
    void test_noTimedPostDeleteSuccessful() {
        setAuth();
        Long postId = 1L;
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId);
        assertTrue(post.getDeleted());
    }

    @Test
    void test_timedPostDeleteSuccessful() {
        setAuth();
        Long postId = 2L;
        timedPost.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId);
        assertTrue(post.getDeleted());
    }

    @Test
    void test_PostDeleteFailedNotTheUsersPost() {
        setAuthWithoutBob();
        Long postId = 2L;
        timedPost.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(NotTheUsersPost.class, () -> postService.deletePost(postId));
    }

    @Test
    void test_postDeleteFailedPostNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Long postId = 1L;
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundByIdException.class, () -> postService.deletePost(postId));
    }

    @Test
    void test_noTimedPostRestoreSuccessful() {
        setAuth();
        Long postId = 1L;
        post.setId(postId);
        post.setDeleted(true);
        post.setCreatedAt(LocalDateTime.now());
        post.setComments(commentList);
        category.setCategoryName("Wine");
        commentList.add(comment);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        post.setCategory(category);
        PostDetails details = postService.restorePost(postId);
        assertFalse(details.getDeleted());
        assertFalse(post.getDeleted());
    }

    @Test
    void test_timedPostRestoreSuccessful() {
        setAuth();
        Long postId = 2L;
        timedPost.setId(postId);
        timedPost.setDeleted(true);
        timedPost.setCreatedAt(LocalDateTime.now());
        timedPost.setPublished(false);
        category.setCategoryName("Wine");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        timedPost.setCategory(category);
        PostDetails details = postService.restorePost(postId);
        assertFalse(details.getDeleted());
        assertFalse(timedPost.getPublished());
    }

    @Test
    void test_PostRestoreFailedNotTheUsersPost() {
        setAuthWithoutBob();
        Long postId = 2L;
        timedPost.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(NotTheUsersPost.class, () -> postService.restorePost(postId));
    }

    @Test
    void test_postRestoreFailedPostNotFound() {
        Long postId = 1L;
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundByIdException.class, () -> postService.restorePost(postId));
    }

    @Test
    void test_editFailedPostNotFound() throws RuntimeException {
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());
        assertThrows(PostNotFoundByIdException.class, () -> postService.editPost(post.getId(), postFormDataUpdate));
        verify(postRepository).findById(post.getId());
    }

    @Test
    void test_editNoTimedPostWithoutPublishTimeSuccessful() {
        setAuth();
        post.setPublishTime(LocalDateTime.now().minusHours(1));
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(true);
        when(postFormDataUpdate.getTitle()).thenReturn("real life is life on the water with new powerboat");
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        PostDetails postDetails = postService.editPost(post.getId(), postFormDataUpdate);
        assertEquals("real life is life on the water with new powerboat", postDetails.getTitle());
        assertTrue(postDetails.getPublished());
    }

    @Test
    void test_editNoTimedPostWithPublishTimeSuccessful() {
        setAuth();
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(true);
        when(postFormDataUpdate.getTitle()).thenReturn("real life is life on the water with new powerboat");
        when(postFormDataUpdate.getPublishTime()).thenReturn(LocalDateTime.now().plusHours(1L));
        when(postFormDataUpdate.getTimedPost()).thenReturn(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        PostDetails postDetails = postService.editPost(post.getId(), postFormDataUpdate);
        assertEquals("real life is life on the water with new powerboat", postDetails.getTitle());
        assertFalse(postDetails.getPublished());

    }

    @Test
    void test_editNoTimedPostWhatChangeTimedPost() {
        setAuth();
        when(postFormDataUpdate.getTitle()).thenReturn("real life is life on the water with new powerboat");
        when(postFormDataUpdate.getPublishTime()).thenReturn(null);
        when(postFormDataUpdate.getTimedPost()).thenReturn(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        post.setPublishTime(LocalDateTime.now().minusHours(1));
        post.setCreatedAt(LocalDateTime.now());
        assertThrows(DontGivePublishTimeException.class, () -> postService.editPost(1L, postFormDataUpdate));

    }

    @Test
    void test_editTimedPostWithoutPublishTime() {
        setAuth();
        post.setPublishTime(LocalDateTime.now().plusHours(1));
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        when(postFormDataUpdate.getTitle()).thenReturn("real life is life on the water with new powerboat");
        when(postFormDataUpdate.getTimedPost()).thenReturn(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        PostDetails postDetails = postService.editPost(post.getId(), postFormDataUpdate);
        assertEquals("real life is life on the water with new powerboat", postDetails.getTitle());
        assertFalse(postDetails.getPublished());
    }

    @Test
    void test_editFailedNotTheUsersPost() {
        setAuthWithoutBob();
        post.setPublishTime(LocalDateTime.now().plusHours(1));
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        assertThrows(NotTheUsersPost.class, () -> postService.editPost(post.getId(), postFormDataUpdate));
    }


    @Test
    void test_saveTestWithoutPublishTime() {
        setAuthWithoutBob();
        when(postFormDataCreate.getTitle()).thenReturn("power boat");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(null);
        when(postRepository.save(any())).thenReturn(post);
        when(customUserService.findCustomUserByUsername(any(UserDetails.class))).thenReturn(customUser);
        post.setCreatedAt(LocalDateTime.now());
        PostDetails postDetails = postService.createPost(postFormDataCreate);
        assertEquals("power boat", postDetails.getTitle());
    }

    @Test
    void test_saveTestWithPublishTimeHasPassed() {
        when(postFormDataCreate.getTitle()).thenReturn("beautiful life");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(LocalDateTime.now().minusHours(1L));

        assertThrows(PublishTimeHasPassedException.class, () -> postService.createPost(postFormDataCreate));

    }

    @Test
    void test_saveTestWithPublishTime() {
        setAuthWithoutBob();
        when(postFormDataCreate.getTitle()).thenReturn("beautiful life");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(LocalDateTime.now().plusHours(1L));
        when(postRepository.save(any())).thenReturn(post);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        PostDetails postDetails = postService.createPost(postFormDataCreate);
        assertFalse(post.getPublished());
    }

    @Test
    void test_savePostWithImg() {
        setAuthWithoutBob();
        when(postFormDataCreate.getTitle()).thenReturn("beautiful life");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(null);
        when(postFormDataCreate.getImgUrl()).thenReturn(IMG_URL_TO_UPLOAD);
        when(cloudinaryService.uploadPhoto(IMG_URL_TO_UPLOAD)).thenReturn(IMG_URL_FROM_CLOUDINARY);
        when(postRepository.save(any())).thenReturn(post);
        post.setCreatedAt(LocalDateTime.now());
        PostDetails postDetails = postService.createPost(postFormDataCreate);
        assertEquals("power boat", postDetails.getTitle());
        assertEquals(IMG_URL_FROM_CLOUDINARY, postDetails.getImgUrl());
    }

    @Test
    void test_savePostWithoutImg() {
        setAuthWithoutBob();
        when(postFormDataCreate.getTitle()).thenReturn("beautiful life");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(null);
        when(postFormDataCreate.getImgUrl()).thenReturn(null);
        when(postRepository.save(any())).thenReturn(post);
        post.setCreatedAt(LocalDateTime.now());
        PostDetails postDetails = postService.createPost(postFormDataCreate);
        assertEquals("power boat", postDetails.getTitle());
        assertNull(postDetails.getImgUrl());
    }

    @Test
    void test_numberOfLikeOfPostAfterLike() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        post.setPublished(true);
        post.setCreatedAt(LocalDateTime.now());
        assertEquals(0, post.getNumberOfLike());
        postService.likePost(post.getId());
        assertEquals(1, post.getNumberOfLike());

    }


    @Test
    void test_savePostWithVideo() {
        when(postFormDataCreate.getTitle()).thenReturn("beautiful life");
        when(postFormDataCreate.getPostBody()).thenReturn("i love my life");
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(category);
        when(postFormDataCreate.getPublishTime()).thenReturn(null);
        when(postFormDataCreate.getImgUrl()).thenReturn(VIDEO_URL_TO_UPLOAD);
        when(cloudinaryService.uploadPhoto(VIDEO_URL_TO_UPLOAD)).thenReturn(VIDEO_URL_FROM_CLOUDINARY);
        when(postRepository.save(any())).thenReturn(post);
        post.setCreatedAt(LocalDateTime.now());
        PostDetails postDetails = postService.createPost(postFormDataCreate);
        assertEquals("power boat", postDetails.getTitle());
        assertEquals(VIDEO_URL_FROM_CLOUDINARY, postDetails.getImgUrl());
    }

    @Test
    void test_savePostFailed() {
        when(categoryService.getCategoryById(postFormDataCreate.getCategoryId())).thenReturn(null);
        assertThrows(CategoryNotFoundByIdException.class, () -> postService.createPost(postFormDataCreate));
    }

    @Test
    void test_getPostListByCategory() {
        when(postRepository.findByCategoryByOrderByCreatedAtDesc(category.getCategoryName())).thenReturn(List.of(post));
        post.setCreatedAt(LocalDateTime.now());
        Assertions.assertThat(postService.getPostListByCategory(category.getCategoryName()))
                .hasSize(1);
    }

    @Test
    void test_getPostListByCategoryEmpty() {
        when(postRepository.findByCategoryByOrderByCreatedAtDesc(category.getCategoryName())).thenReturn(List.of());
        assertThat(postService.getPostListByCategory(category.getCategoryName()).isEmpty());
    }

    @Test
    void test_getPostListByUsername() {
        when(postRepository.findByUsernameByOrderByCreatedAtDesc(customUser.getUsername())).thenReturn(List.of(post));
        post.setCreatedAt(LocalDateTime.now());
        Assertions.assertThat(postService.getPostListByUsername(customUser.getUsername()))
                .hasSize(1);
    }

    @Test
    void test_getPostListItems() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        post.setCreatedAt(LocalDateTime.now());
        List<PostListItem> commentList = postService.getPostListItems();
        assertEquals(1, commentList.size());
    }

    @Test
    void test_getPostDetailsById() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(postRepository.getOne(1l)).thenReturn(post);
        post.setCreatedAt(LocalDateTime.now());
        post.setTitle("wine");
        org.junit.jupiter.api.Assertions.assertEquals(post.getTitle(), postService.getPostDetailsById(1l).getTitle());
    }

    @Test
    void test_getPostDetailsByIdFailed() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        assertThrows(PostNotFoundByIdException.class, () -> postService.getPostDetailsById(1L));

    }

    @Test
    void test_getPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        post.setTitle("wine");
        post.setCreatedAt(LocalDateTime.now());
        org.junit.jupiter.api.Assertions.assertEquals(post.getTitle(), postService.getPostById(1l).getTitle());
    }

    private void setAuth() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(postService.getLoggedInUser()).thenReturn(applicationUser);
        when(applicationUser.getUsername()).thenReturn("bob");
    }

    private void setAuthWithoutBob() {
        UserDetails applicationUser = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        when(postService.getLoggedInUser()).thenReturn(applicationUser);
    }

}

