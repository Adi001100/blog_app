package blog.dto;

import blog.domain.CustomUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<PostDetails> getPostDetailsList() {
        return postDetailsList;
    }

    public void setPostDetailsList(List<PostDetails> postDetailsList) {
        this.postDetailsList = postDetailsList;
    }
}
