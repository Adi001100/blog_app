package blog.domain;

import blog.dto.CommentFormDataCreate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "comment_body", columnDefinition = "TEXT")
    private String commentBody;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    private Post post;

    @Column(name = "img_url")
    private String imgUrl;

    public Comment(CommentFormDataCreate commentFormData, Post post) {
        this.author = commentFormData.getAuthor();
        this.commentBody = commentFormData.getCommentBody();
        this.createdAt = LocalDateTime.now();
        this.post = post;
        this.imgUrl = commentFormData.getImgUrl();
    }
}
