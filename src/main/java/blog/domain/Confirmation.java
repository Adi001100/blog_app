package blog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@Entity
@Table(name = "confirmations")
public class Confirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;


    private LocalDateTime createdDate;

    @OneToOne(targetEntity = CustomUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_email")
    private CustomUser customUser;

    public Confirmation(CustomUser customUser) {
        this.customUser = customUser;
        this.createdDate = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
    }
}
