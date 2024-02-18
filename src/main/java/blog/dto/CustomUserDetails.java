package blog.dto;

import blog.domain.CustomUser;
import lombok.Getter;

@Getter
public class CustomUserDetails {

    private final String username;
    private final String fullName;

    public CustomUserDetails(CustomUser customUser) {
        this.username = customUser.getUsername();
        this.fullName = customUser.getFullName();
    }
}
