package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryNotFoundByIdException extends RuntimeException {

    private final Long id;
}
