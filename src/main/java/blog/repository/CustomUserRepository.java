package blog.repository;

import blog.domain.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

    CustomUser findCustomUserByUserEmail(String email);

    CustomUser findCustomUserByUsername(String username);

    @Query("Select c  from CustomUser c where c.isEnabled = false and c.registrationDate < :registrationTime")
    List<CustomUser> findCustomUserByEnabled(LocalDateTime registrationTime);
}
