package blog.exception;

public class PostNotFoundByIdException extends RuntimeException {

    private final Long id;

    public PostNotFoundByIdException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
