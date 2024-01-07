package blog.controller;

import blog.dto.PostDetails;
import blog.dto.PostFormDataCreate;
import blog.dto.PostFormDataUpdate;
import blog.dto.PostListItem;
import blog.service.ChatGptService;
import blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private PostService postService;

    private ChatGptService chatGptService;

    @Autowired
    public PostController(PostService postService, ChatGptService chatGptService) {
        this.postService = postService;
        this.chatGptService = chatGptService;
    }

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Save a post.")
    @ApiResponse(responseCode = "201", description = "Post has been saved.")
    public ResponseEntity<PostDetails> createPost(@Valid @RequestBody PostFormDataCreate postFormData) {
        logger.info("Http request GET api/posts, body: " + postFormData.toString());
        PostDetails postCreated = postService.createPost(postFormData);
        logger.info("Post has been saved!");
        return new ResponseEntity(postCreated, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all posts.")
    @ApiResponse(responseCode = "200", description = "Posts has been listed.")
    public ResponseEntity<List<PostListItem>> getPostList() {
        logger.info("Http request GET /api/posts");
        logger.info("Post has been listed!");
        return new ResponseEntity<>(postService.getPostListItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find post with ID.")
    @ApiResponse(responseCode = "200", description = "Post has been found.")
    public ResponseEntity<PostDetails> getPost(@PathVariable("id") Long id) {
        logger.info("Http request GET /api/posts/{id} path variable: " + id, Level.INFO);
        PostDetails postDetails = postService.getPostDetailsById(id);
        logger.info("Post has been found by ID: " + id);
        return new ResponseEntity<>(postDetails, HttpStatus.OK);

    }

    @GetMapping("/category")
    @Operation(summary = "Find post with category.")
    @ApiResponse(responseCode = "200", description = "Post has been listed with category.")
    public ResponseEntity<List<PostListItem>> getPostListByCategory(@RequestParam(value = "category", required = false) String category) {
        logger.info("Http request GET /api/posts/category path variable: " + category, Level.INFO);
        List<PostListItem> postListItem = postService.getPostListByCategory(category);
        logger.info("Post has been found by category: " + category);
        return new ResponseEntity<>(postListItem, HttpStatus.OK);
    }

    @GetMapping("/username")
    @Operation(summary = "Find post with username.")
    @ApiResponse(responseCode = "200", description = "Post has been listed with username.")
    public ResponseEntity<List<PostListItem>> getPostListByUsername(@RequestParam(value = "username", required = false) String username) {
        logger.info("Http request GET /api/posts/username path variable: " + username, Level.INFO);
        List<PostListItem> postListItem = postService.getPostListByUsername(username);
        logger.info("Post has been found by username: " + username);
        return new ResponseEntity<>(postListItem, HttpStatus.OK);
    }

    @PutMapping("edit/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Update post.")
    @ApiResponse(responseCode = "202", description = "Post has been updated.")
    public ResponseEntity<PostDetails> editPost(@PathVariable("id") Long id,
                                                @Valid @RequestBody PostFormDataUpdate postFormDataUpdate) {
        logger.info("Http request PUT /api/post/edit/{id} with variable" + id);
        PostDetails postDetails = postService.editPost(id, postFormDataUpdate);
        logger.info("Post has been updated");
        return new ResponseEntity<>(postDetails, HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Delete post.")
    @ApiResponse(responseCode = "202", description = "Post has been deleted.")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        logger.info("Http request DELETE /api/posts/delete/{id} path variable:" + id, Level.INFO);
        postService.deletePost(id);
        logger.info("Post has been deleted by ID: " + id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/restore/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Restore post.")
    @ApiResponse(responseCode = "200", description = "Post has been restored.")
    public ResponseEntity<PostDetails> restorePost(@PathVariable("id") Long id) {
        logger.info("Http request PUT /api/posts/restore/{id} path variable:" + id);
        PostDetails postDetails = postService.restorePost(id);
        logger.info("Post has been restored by ID: " + id);
        return new ResponseEntity<>(postDetails, HttpStatus.OK);
    }

    @PutMapping("/like/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Like post")
    @ApiResponse(responseCode = "200", description = "The post has been liked")
    public ResponseEntity<PostDetails> likePost(@PathVariable("id") Long id) {
        logger.info("Http request PUT /api/posts/like/{id} with path variable:" + id);
        PostDetails postDetails = postService.likePost(id);
        logger.info("Add new like to post");
        return new ResponseEntity<>(postDetails, HttpStatus.OK);
    }
}
