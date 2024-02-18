package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PostDetails {

    private Long id;
    private String title;
    private String postBody;
    private String imgUrl;
    private String videoUrl;
    private String createdAt;
    private List<CommentDetails> comments;
    private Boolean isDeleted;
    private Boolean isPublished;
    private String category;
    private Integer numberOfLike;

    public PostDetails(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.postBody = post.getPostBody();
        this.imgUrl = post.getImgUrl();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(post.getCreatedAt());
        if (post.getComments() == null) {
            this.comments = new ArrayList<>();
        } else {
            this.comments = post.getComments().stream()
                    .map(CommentDetails::new)
                    .collect(Collectors.toList());
        }
        this.category = post.getCategory().getCategoryName();
        this.isDeleted = post.getIsDeleted();
        this.isPublished = post.getPublished();
        this.numberOfLike = post.getNumberOfLike();
        this.videoUrl = post.getVideoUrl();
    }
}
