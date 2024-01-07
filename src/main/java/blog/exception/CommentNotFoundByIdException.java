package blog.exception;

public class CommentNotFoundByIdException extends RuntimeException {
    private final Long id;

    public CommentNotFoundByIdException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
