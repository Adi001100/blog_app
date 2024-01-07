package blog.dto;

public class CommentFormDataUpdate {

    private Long postId;

    private String author;

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
}
