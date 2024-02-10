package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDetails {
    @Schema(example = "java")
    private String categoryName;

    @Schema(example = "ideas for Java 1.0")
    private String description;

    @Schema(example = "1996.01.01")
    private String createdAt;

    public CategoryDetails(Category category) {
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(category.getCreatedAt());
    }
}
