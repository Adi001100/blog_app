package blog.domain;

import blog.config.UserRole;
import blog.dto.CustomUserFormData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "custom_user")
@NoArgsConstructor
@Getter
@Setter
public class CustomUser {

    @Id
    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private Integer yearOfBirth;

    private boolean isEnabled;

    @OneToMany(mappedBy = "customUser")
    private List<Post> postList;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private List<UserRole> roles;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    public CustomUser(CustomUserFormData data) {
        this.userEmail = data.getUserEmail();
        this.password = data.getPassword();
        this.username = data.getUsername();
        this.fullName = data.getFullName();
        this.yearOfBirth = data.getYearOfBirth();
        this.registrationDate = LocalDateTime.now();
    }
}
