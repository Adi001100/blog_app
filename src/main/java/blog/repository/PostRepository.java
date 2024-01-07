package blog.repository;

import blog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("Select p from Post p where p.published = true and p.isDeleted = false order by p.createdAt desc")
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByPublishTimeBefore(LocalDateTime publishTime);

    @Query("Select p from Post p join p.category c where c.categoryName = :category and p.published = true and p.isDeleted = false order by p.createdAt desc")
    List<Post> findByCategoryByOrderByCreatedAtDesc(String category);

    @Query("Select p from Post p join p.customUser c where c.username = :username and p.published = true and p.isDeleted = false order by p.createdAt desc")
    List<Post> findByUsernameByOrderByCreatedAtDesc(String username);
}
