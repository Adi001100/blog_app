package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryNotFoundByCategoryNameException extends RuntimeException {

    private final String categoryName;
}
