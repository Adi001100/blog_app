package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Category;

public class CategoryListItem {
    private Long id;
    private String categoryName;
    private String createdAt;

    private Integer numberOfPosts;

    public CategoryListItem(Category category) {
        this.id = category.getId();
        this.categoryName = category.getCategoryName();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(category.getCreatedAt());
        this.numberOfPosts = category.getPostList().size();
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getNumberOfPosts() {
        return numberOfPosts;
    }
}
