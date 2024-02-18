package blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public class CategoryFormData {

    @NotBlank(message = "Must not be blank!")
    @Schema(example = "java")
    @Setter
    private String categoryName;

    @Setter
    @Schema(example = "ideas for java")
    private String description;
}
