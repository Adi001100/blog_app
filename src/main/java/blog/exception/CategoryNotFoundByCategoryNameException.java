package blog.exception;

public class CategoryNotFoundByCategoryNameException extends RuntimeException {

    private final String categoryName;

    public CategoryNotFoundByCategoryNameException(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
