package blog.domain;

import blog.dto.PostFormDataCreate;
import blog.exception.PublishTimeHasPassedException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "post_body", columnDefinition = "TEXT")
    private String postBody;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "creation_at")
    private LocalDateTime createdAt;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "published")
    private Boolean published;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @OrderBy(value = "createdAt desc")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "user_email")
    private CustomUser customUser;

    private Integer numberOfLike;

    public Post(PostFormDataCreate postFormData, Category category) {
        this.title = postFormData.getTitle();
        this.postBody = postFormData.getPostBody();
        this.createdAt = LocalDateTime.now();
        if (postFormData.getPublishTime() == null) {
            this.publishTime = LocalDateTime.now();
            this.published = true;
        } else if (postFormData.getPublishTime().isBefore(LocalDateTime.now())) {
            throw new PublishTimeHasPassedException(postFormData.getPublishTime());
        } else {
            this.publishTime = postFormData.getPublishTime();
            this.published = false;
        }
        this.category = category;
        this.isDeleted = false;
        this.numberOfLike = 0;
        this.videoUrl = postFormData.getVideoUrl();
    }
}
