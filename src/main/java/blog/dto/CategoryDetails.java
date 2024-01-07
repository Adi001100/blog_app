package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Category;

public class CategoryDetails {
    private String categoryName;

    private String description;

    private String createdAt;

    public CategoryDetails(Category category) {
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(category.getCreatedAt());
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
