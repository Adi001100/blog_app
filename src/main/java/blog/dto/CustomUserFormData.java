package blog.dto;

import blog.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomUserFormData {

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    @Email(message = "Not valid email!")
    private String userEmail;

    @ValidPassword
    private String password;

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    private String username;

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    private String fullName;
    //todo
    private Integer yearOfBirth;

}
