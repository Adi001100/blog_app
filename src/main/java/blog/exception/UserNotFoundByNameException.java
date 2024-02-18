package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNotFoundByNameException extends RuntimeException {

    private final String userName;
}
