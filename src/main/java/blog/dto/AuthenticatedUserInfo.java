package blog.dto;

import blog.config.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class AuthenticatedUserInfo {

    private final String username;
    private final List<UserRole> roles;

    public AuthenticatedUserInfo(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.roles = parseRoles(userDetails);
    }

    private List<UserRole> parseRoles(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(authority -> UserRole.valueOf(authority.getAuthority()))
                .collect(Collectors.toList());
    }
}
