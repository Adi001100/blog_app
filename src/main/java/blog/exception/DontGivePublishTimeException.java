package blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class DontGivePublishTimeException extends RuntimeException {

    private final LocalDateTime publishTime;
}
