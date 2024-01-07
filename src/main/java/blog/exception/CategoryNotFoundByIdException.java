package blog.exception;

public class CategoryNotFoundByIdException extends RuntimeException {

    private final Long id;

    public CategoryNotFoundByIdException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
