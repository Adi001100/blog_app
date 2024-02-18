package blog.domain;

import blog.dto.CategoryFormData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category")
    private List<Post> postList;

    public Category(CategoryFormData categoryFormData) {
        this.categoryName = categoryFormData.getCategoryName();
        this.description = categoryFormData.getDescription();
        this.createdAt = LocalDateTime.now();
    }
}
