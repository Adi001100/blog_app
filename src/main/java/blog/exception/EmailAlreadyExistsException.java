package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;
}
