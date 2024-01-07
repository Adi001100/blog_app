package blog.dto;

import blog.domain.CustomUser;

public class CustomUserDetails {

    private String username;

    private String fullName;

    public CustomUserDetails(CustomUser customUser) {
        this.username = customUser.getUsername();
        this.fullName = customUser.getFullName();
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }
}
