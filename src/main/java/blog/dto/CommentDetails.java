package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Comment;

public class CommentDetails {

    private String author;
    private String commentBody;
    private String createdAt;
    private String imgUrl;

    public CommentDetails(Comment comment) {
        this.author = comment.getAuthor();
        this.commentBody = comment.getCommentBody();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(comment.getCreatedAt());
        this.imgUrl = comment.getImgUrl();
    }

    public String getAuthor() {
        return author;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
