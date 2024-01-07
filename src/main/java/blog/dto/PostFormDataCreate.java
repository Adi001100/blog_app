package blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PostFormDataCreate {

    @NotBlank(message = "Must not be blank!")
    private String title;
    private String postBody;
    private String imgUrl;
    private String videoUrl;
    private LocalDateTime publishTime;
    @NotNull(message = "Must not be null!")
    private Long categoryId;

    public PostFormDataCreate() {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
