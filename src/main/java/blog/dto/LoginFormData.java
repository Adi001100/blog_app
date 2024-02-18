package blog.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class LoginFormData {

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    @Email(message = "Not valid email!")
    private String userEmail;

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    private String password;

    @Override
    public String toString() {
        return "LoginFormData{" +
                "userEmail='" + userEmail + '\'' +
                '}';
    }
}
