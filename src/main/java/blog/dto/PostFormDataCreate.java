package blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostFormDataCreate {

    @NotBlank(message = "Must not be blank!")
    @Schema(example = "About Java")
    private String title;

    @Schema(example = "Java is a powerful, object-oriented programming language developed by Sun Microsystems. It's known for its simplicity, portability, and security. Used in a variety of applications, from web and mobile development to large-scale systems.")
    private String postBody;

    @Schema(example = "/Users/user/Desktop/blogDemo/152760.png")
    private String imgUrl;

    @Schema(example = "/Users/user/Desktop/blogDemo/152760.mp4")
    private String videoUrl;

    @Schema(example = "2024-02-18 13:18:51.822484")
    private LocalDateTime publishTime;

    @NotNull(message = "Must not be null!")
    @Schema(example = "1")
    private Long categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
}
