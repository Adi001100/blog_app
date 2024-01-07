package blog.exception;

import java.time.LocalDateTime;

public class PublishTimeHasPassedException extends RuntimeException {

    private final LocalDateTime publishTime;

    public PublishTimeHasPassedException(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }
}
