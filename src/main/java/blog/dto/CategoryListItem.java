package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Category;
import lombok.Getter;

@Getter
public class CategoryListItem {

    private final Long id;
    private final String categoryName;
    private final String createdAt;
    private final Integer numberOfPosts;

    public CategoryListItem(Category category) {
        this.id = category.getId();
        this.categoryName = category.getCategoryName();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(category.getCreatedAt());
        this.numberOfPosts = category.getPostList().size();
    }
}
