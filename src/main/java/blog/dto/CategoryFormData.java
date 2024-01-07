package blog.dto;

import javax.validation.constraints.NotBlank;

public class CategoryFormData {

    @NotBlank(message = "Must not be blank!")
    private String categoryName;

    private String description;

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public CategoryFormData(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
