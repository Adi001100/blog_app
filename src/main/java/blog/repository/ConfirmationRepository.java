package blog.repository;

import blog.domain.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Confirmation findByToken(String toke);

    Boolean existsByToken(String token);

    Confirmation findByCustomUser_UserEmail(String email);
}
