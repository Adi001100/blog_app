package blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostFormDataUpdate {

    private String title;
    private String postBody;
    private String imgUrl;
    private LocalDateTime publishTime;
    private final Boolean timedPost = false;
    private Long categoryId;
    private Integer numberOfLike;


}
