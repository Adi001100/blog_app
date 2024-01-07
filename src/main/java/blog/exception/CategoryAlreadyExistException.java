package blog.exception;

public class CategoryAlreadyExistException extends RuntimeException {

    private final String categoryName;

    public CategoryAlreadyExistException(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
