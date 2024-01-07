package blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class PostFormDataUpdate {
    private String title;
    private String postBody;
    private String imgUrl;
    private LocalDateTime publishTime;
    private Boolean timedPost = false;
    private Long categoryId;
    private Integer numberOfLike;

    public String getTitle() {
        return title;
    }

    public String getPostBody() {
        return postBody;
    }

    public Boolean getTimedPost() {
        return timedPost;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Integer getNumberOfLike() {
        return numberOfLike;
    }
}
