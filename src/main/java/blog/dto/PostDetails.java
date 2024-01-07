package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.isDeleted = post.getDeleted();
        this.isPublished = post.getPublished();
        this.numberOfLike = post.getNumberOfLike();
        this.videoUrl = post.getVideoUrl();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPostBody() {
        return postBody;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<CommentDetails> getComments() {
        return comments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setComments(List<CommentDetails> comments) {
        this.comments = comments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public Integer getNumberOfLike() {
        return numberOfLike;
    }

    public void setNumberOfLike(Integer numberOfLike) {
        this.numberOfLike = numberOfLike;
    }
}
