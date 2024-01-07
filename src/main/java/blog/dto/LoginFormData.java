package blog.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginFormData {

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    @Email(message = "Not valid email!")
    private String userEmail;

    @NotBlank(message = "Must be not blank!")
    @Size(min = 3)
    private String password;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginFormData{" +
                "userEmail='" + userEmail + '\'' +
                '}';
    }
}
