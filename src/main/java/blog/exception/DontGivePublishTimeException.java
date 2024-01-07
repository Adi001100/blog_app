package blog.exception;

import java.time.LocalDateTime;

public class DontGivePublishTimeException extends RuntimeException {

    private final LocalDateTime publishTime;

    public DontGivePublishTimeException(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }
}
