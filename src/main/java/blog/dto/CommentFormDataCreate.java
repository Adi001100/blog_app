package blog.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CommentFormDataCreate {

    private Long postId;

    private String author;

    private String imgUrl;

    @NotEmpty(message = "Must not be empty!")
    @Size(max = 500)
    private String commentBody;

    public Long getPostId() {
        return postId;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
