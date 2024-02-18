package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentNotFoundByIdException extends RuntimeException {
    private final Long id;
}
