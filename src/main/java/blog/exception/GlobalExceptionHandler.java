package blog.exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationError(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        logger.error("Request JSON could no be parsed: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError("JSON_PARSE_ERROR", "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError("ILLEGAL_ARGUMENT_ERROR", "An illegal argument has been passed to the method.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        logger.error("An unexpected error occurred: ", t);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError body = new ApiError("UNCLASSIFIED_ERROR", "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<List<ValidationError>> handleCategoryAlreadyHave(CategoryAlreadyExistException exception) {
        logger.error("Category already have: ", exception);

        ValidationError validationError = new ValidationError("categoryName", "Category already exist: " + exception.getCategoryName());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<List<ValidationError>> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        logger.error("Email already exists: ", exception);

        ValidationError validationError = new ValidationError("email", "Email already exists: " + exception.getEmail());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundByEmailException.class)
    public ResponseEntity<List<ValidationError>> handleUserNotFoundByEmail(UserNotFoundByEmailException exception) {
        logger.error("User not found by email: ", exception);

        ValidationError validationError = new ValidationError("email", "User not found by email: " + exception.getEmail());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<List<ValidationError>> handleWrongPassword(WrongPasswordException exception) {
        logger.error("Wrong password ", exception);

        ValidationError validationError = new ValidationError("password", "Wrong password, please try again!");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundByIdException.class)
    public ResponseEntity<List<ValidationError>> handlePostNotFound(PostNotFoundByIdException exception) {
        logger.error("Post not found by id: ", exception);

        ValidationError validationError = new ValidationError("id", "Post not found by id: " + exception.getId());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<List<ValidationError>> handleUsernameAlreadyExists(UsernameAlreadyExistsException exception) {
        logger.error("Username already exists: ", exception);

        ValidationError validationError = new ValidationError("username", "Username already exists: " + exception.getUsername());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundByNameException.class)
    public ResponseEntity<List<ValidationError>> handleUserNotFoundByName(UserNotFoundByNameException exception) {
        logger.error("User not found by name: ", exception);

        ValidationError validationError = new ValidationError("username", "User not found by username: " + exception.getUserName());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PhotoUploadFailedException.class)
    public ResponseEntity<List<ValidationError>> handlePhotoCanNotUpload(PhotoUploadFailedException exception) {
        logger.error("Photo upload failed", exception);

        ValidationError validationError = new ValidationError("photo", "Photo upload failed, wrong URL!");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VideoUploadFailedException.class)
    public ResponseEntity<List<ValidationError>> handleVideoCanNotUpload(VideoUploadFailedException exception) {
        logger.error("Video upload failed", exception);

        ValidationError validationError = new ValidationError("video", "Video upload failed, wrong URL!");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PublishTimeHasPassedException.class)
    public ResponseEntity<List<ValidationError>> handlePublishTimeHasPassed(PublishTimeHasPassedException exception) {
        logger.error("The publish time has passed: ", exception);

        ValidationError validationError = new ValidationError("publishTime", "The publish time has passed: " + exception.getPublishTime());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DontGivePublishTimeException.class)
    public ResponseEntity<List<ValidationError>> handleDontGivePublishTime(DontGivePublishTimeException exception) {
        logger.error("The old publish time has passed amd the new publish time is null!", exception);

        ValidationError validationError = new ValidationError("publishTime", "The old publish time has passed: " + exception.getPublishTime() + " and the new publish time is null!");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentNotFoundByIdException.class)
    public ResponseEntity<List<ValidationError>> handleCommentNotFound(CommentNotFoundByIdException exception) {
        logger.error("Comment not found by id: ", exception);

        ValidationError validationError = new ValidationError("id", "Comment not found by id: " + exception.getId());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundByIdException.class)
    public ResponseEntity<List<ValidationError>> handleCategoryNotFound(CategoryNotFoundByIdException exception) {
        logger.error("Category not found by id: ", exception);

        ValidationError validationError = new ValidationError("id", "Category not found by id: " + exception.getId());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotTheUsersPost.class)
    public ResponseEntity<List<ValidationError>> handleNotTheUsersPost(NotTheUsersPost exception) {
        logger.error("Wrong user for post", exception);

        ValidationError validationError = new ValidationError("post", "This is not your post.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotTheUsersComment.class)
    public ResponseEntity<List<ValidationError>> handleNotTheUsersComment(NotTheUsersComment exception) {
        logger.error("Wrong user for comment", exception);

        ValidationError validationError = new ValidationError("comment", "This is not your comment.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundByCategoryNameException.class)
    public ResponseEntity<List<ValidationError>> handleCategoryNotFoundByCategoryName(CategoryNotFoundByCategoryNameException exception) {
        logger.error("Category not found by category name: " + exception.getCategoryName(), exception);

        ValidationError validationError = new ValidationError("categoryName", "Category not found by category name: " + exception.getCategoryName());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

}
