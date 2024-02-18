package blog.dto;

import blog.domain.CustomUser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class CustomUserPage {

    private String userName;
    private Integer year;
    private List<PostDetails> postDetailsList;

    public CustomUserPage(CustomUser customUser) {
        this.userName = customUser.getUsername();
        this.year = customUser.getYearOfBirth();
        if (customUser.getPostList() == null) {
            this.postDetailsList = new ArrayList<>();
        } else {
            this.postDetailsList = customUser.getPostList().stream()
                    .map(PostDetails::new)
                    .collect(Collectors.toList());
        }
    }
}
