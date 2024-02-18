package blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentFormDataCreate {

    private Long postId;
    private String author;
    private String imgUrl;

    @NotEmpty(message = "Must not be empty!")
    @Size(max = 500)
    private String commentBody;
}
