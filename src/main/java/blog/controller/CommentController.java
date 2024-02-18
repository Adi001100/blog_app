package blog.controller;

import blog.dto.CommentDetails;
import blog.dto.CommentFormDataCreate;
import blog.dto.CommentFormDataUpdate;
import blog.exception.CommentNotFoundByIdException;
import blog.exception.NotTheUsersComment;
import blog.exception.PostNotFoundByIdException;
import blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private CommentService commentService;


    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Save a comment.")
    @ApiResponse(
            responseCode = "201",
            description = "Comment has been saved.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Post not found by id",
            content = @Content(
                    schema = @Schema(implementation = PostNotFoundByIdException.class)
            )
    )
    public ResponseEntity<CommentDetails> createComment(@Valid @RequestBody CommentFormDataCreate commentFormDataCreate) {
        logger.info("Http request GET api/comments, body: {}", commentFormDataCreate);
        CommentDetails commentCreated = commentService.createComment(commentFormDataCreate);
        logger.info("Comment has been saved!");
        return new ResponseEntity<>(commentCreated, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all comments.")
    @ApiResponse(
            responseCode = "200",
            description = "Comments has been listed.")
    public ResponseEntity<List<CommentDetails>> getCommentList() {
        logger.info("Http request GET /api/posts");
        logger.info("Comment has been listed!");
        return new ResponseEntity<>(commentService.getCommentList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find comment with ID.")
    @ApiResponse(
            responseCode = "200",
            description = "Comment has been found.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Comment not found by id",
            content = @Content(
                    schema = @Schema(implementation = CommentNotFoundByIdException.class)
            )
    )
    public ResponseEntity<CommentDetails> getComment(@PathVariable("id") Long id) {
        logger.info("Http request GET /api/categories/{} path variable: ", id);
        CommentDetails commentDetails = commentService.getCommentDetailsById(id);
        logger.info("Comment has been found by ID: {}", id);
        return new ResponseEntity<>(commentDetails, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Delete comment with ID.")
    @ApiResponse(
            responseCode = "202",
            description = "Comment has been deleted.")
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Comment not found by id",
            content = @Content(
                    schema = @Schema(implementation = CommentNotFoundByIdException.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Not the users comment",
            content = @Content(
                    schema = @Schema(implementation = NotTheUsersComment.class)
            )
    )
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        logger.info("Http request DELETE /api/posts/{} path variable:", id);
        commentService.deleteComment(id);
        logger.info("Comment has been deleted by ID: {}", id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("edit/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Update comment.")
    @ApiResponse(
            responseCode = "202",
            description = "Comment has been updated.")
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
                    "Comment not found by id",
            content = @Content(
                    schema = @Schema(implementation = CommentNotFoundByIdException.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Bad Request \n " +
                    "Not the users comment",
            content = @Content(
                    schema = @Schema(implementation = NotTheUsersComment.class)
            )
    )
    public ResponseEntity<CommentDetails> editComment(@PathVariable("id") Long id,
                                                      @Valid @RequestBody CommentFormDataUpdate commentFormDataUpdate) {
        logger.info("Http request PUT /api/comment/edit/{} with variable", id);
        CommentDetails commentDetails = commentService.editComment(id, commentFormDataUpdate);
        logger.info("Comment has been updated");
        return new ResponseEntity<>(commentDetails, HttpStatus.ACCEPTED);

    }
}
