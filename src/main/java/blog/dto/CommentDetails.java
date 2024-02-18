package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Comment;
import lombok.Getter;

@Getter
public class CommentDetails {

    private final String author;
    private final String commentBody;
    private final String createdAt;
    private final String imgUrl;

    public CommentDetails(Comment comment) {
        this.author = comment.getAuthor();
        this.commentBody = comment.getCommentBody();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(comment.getCreatedAt());
        this.imgUrl = comment.getImgUrl();
    }
}
