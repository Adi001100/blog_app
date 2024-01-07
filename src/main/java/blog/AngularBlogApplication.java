package blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AngularBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngularBlogApplication.class, args);
    }

}
