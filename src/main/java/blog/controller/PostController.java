package blog.controller;

import blog.dto.PostDetails;
import blog.dto.PostFormDataCreate;
import blog.dto.PostFormDataUpdate;
import blog.dto.PostListItem;
import blog.exception.*;
import blog.service.ChatGptService;
import blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@Tag(
        name = "CRUD REST APIs for Posts in BLOGMASTER",
        description = "to CREATE, FETCH, UPDATE, nd DELETE post(s)"
)
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Save post REST API")
    @ApiResponse(
            responseCode = "201",
            description = "Post has been saved.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Category not found by id",
            content = @Content(
                    schema = @Schema(implementation = CategoryNotFoundByIdException.class)
            )
    )
    public ResponseEntity<PostDetails> createPost(@Valid @RequestBody PostFormDataCreate postFormData) {
        logger.info("Http request GET api/posts, body: {}", postFormData);
        PostDetails postCreated = postService.createPost(postFormData);
        logger.info("Post has been saved!");
        return new ResponseEntity<>(postCreated, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all posts REST API")
    @ApiResponse(
            responseCode = "200",
            description = "Posts has been listed.")
    public ResponseEntity<List<PostListItem>> getPostList() {
        logger.info("Http request GET /api/posts");
        logger.info("Post has been listed!");
        return new ResponseEntity<>(postService.getPostListItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find post with ID REST API")
    @ApiResponse(
            responseCode = "200",
            description = "Post has been found.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Post not found by id",
            content = @Content(
                    schema = @Schema(implementation = PostNotFoundByIdException.class)
            )
    )
    public ResponseEntity<PostDetails> getPost(@PathVariable("id") Long id) {
        logger.info("Http request GET /api/posts/{} path variable: {}", id, id);
        PostDetails postDetails = postService.getPostDetailsById(id);
        logger.info("Post has been found by ID: {}", id);
        return new ResponseEntity<>(postDetails, HttpStatus.OK);

    }

    @GetMapping("/category")
    @Operation(summary = "Find post with category REST API")
    @ApiResponse(
            responseCode = "200",
            description = "Post has been listed with category.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Category not found by category name",
            content = @Content(
                    schema = @Schema(implementation = CategoryNotFoundByCategoryNameException.class)
            )
    )
    public ResponseEntity<List<PostListItem>> getPostListByCategory(
            @RequestParam(value = "category", required = false) String category) {
        logger.info("Http request GET /api/posts/category path variable: {}", category);
        List<PostListItem> postListItem = postService.getPostListByCategory(category);
        logger.info("Post has been found by category: {}", category);
        return new ResponseEntity<>(postListItem, HttpStatus.OK);
    }

    @GetMapping("/username")
    @Operation(summary = "Find post with username REST API")
    @ApiResponse(
            responseCode = "200",
            description = "Post has been listed with username.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "User not found by name",
            content = @Content(
                    schema = @Schema(implementation = UserNotFoundByNameException.class)
            )
    )
    public ResponseEntity<List<PostListItem>> getPostListByUsername(
            @RequestParam(value = "username", required = false) String username) {
        logger.info("Http request GET /api/posts/username path variable: {}", username);
        List<PostListItem> postListItem = postService.getPostListByUsername(username);
        logger.info("Post has been found by username: {}", username);
        return new ResponseEntity<>(postListItem, HttpStatus.OK);
    }

    @PutMapping("edit/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Update post REST API")
    @ApiResponse(
            responseCode = "202",
            description = "Post has been updated.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "User not found by name",
            content = @Content(
                    schema = @Schema(implementation = UserNotFoundByNameException.class)
            )
    )
    public ResponseEntity<PostDetails> editPost(@PathVariable("id") Long id,
                                                @Valid @RequestBody PostFormDataUpdate postFormDataUpdate) {
        logger.info("Http request PUT /api/post/edit/{} with variable: {}", id, id);
        PostDetails postDetails = postService.editPost(id, postFormDataUpdate);
        logger.info("Post has been updated");
        return new ResponseEntity<>(postDetails, HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Delete post REST API")
    @ApiResponse(
            responseCode = "202",
            description = "Post has been deleted.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Post not found by id",
            content = @Content(
                    schema = @Schema(implementation = PostNotFoundByIdException.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Not the users post",
            content = @Content(
                    schema = @Schema(implementation = NotTheUsersPost.class)
            )
    )
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        logger.info("Http request DELETE /api/posts/delete/{} path variable: {}", id, id);
        postService.deletePost(id);
        logger.info("Post has been deleted by ID: {}", id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/restore/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Restore post REST API")
    @ApiResponse(
            responseCode = "200",
            description = "Post has been restored.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Post not found by id",
            content = @Content(
                    schema = @Schema(implementation = PostNotFoundByIdException.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Not the users post",
            content = @Content(
                    schema = @Schema(implementation = NotTheUsersPost.class)
            )
    )

    public ResponseEntity<PostDetails> restorePost(@PathVariable("id") Long id) {
        logger.info("Http request PUT /api/posts/restore/{} path variable: {}", id, id);
        PostDetails postDetails = postService.restorePost(id);
        logger.info("Post has been restored by ID: {}", id);
        return new ResponseEntity<>(postDetails, HttpStatus.OK);
    }

    @PutMapping("/like/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Like post REST API")
    @ApiResponse(
            responseCode = "200",
            description = "The post has been liked")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Post not found by id",
            content = @Content(
                    schema = @Schema(implementation = PostNotFoundByIdException.class)
            )
    )
    public ResponseEntity<PostDetails> likePost(@PathVariable("id") Long id) {
        logger.info("Http request PUT /api/posts/like/{} with path variable: {}", id, id);
        PostDetails postDetails = postService.likePost(id);
        logger.info("Add new like to post");
        return new ResponseEntity<>(postDetails, HttpStatus.OK);
    }
}
