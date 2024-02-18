package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNotFoundByEmailException extends RuntimeException {

    private final String email;
}
