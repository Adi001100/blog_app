package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ApiError {
    private String errorCode;
    private String error;
    private String details;
}

