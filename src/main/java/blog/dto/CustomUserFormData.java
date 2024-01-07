package blog.dto;

import blog.validation.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    public String getUserEmail() {
        return userEmail;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
}
