package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsernameAlreadyExistsException extends RuntimeException {

    private final String username;
}
