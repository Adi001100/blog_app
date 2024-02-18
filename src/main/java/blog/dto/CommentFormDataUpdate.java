package blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentFormDataUpdate {

    private Long postId;
    private String author;
    private String commentBody;
}
