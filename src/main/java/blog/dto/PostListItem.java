package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Post;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostListItem {

    private Long id;
    private String title;
    private String postBodyShortened;
    private String imgUrl;
    private String createdAt;
    private Integer numberOfComments;
    private String category;
    private String username;

    public PostListItem(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();

        this.postBodyShortened = Stream.of(post.getPostBody())
                .map(string -> string.substring(0, Math.min(200, string.length())))
                .map(string -> string.substring(0, string.contains(" ") && post.getPostBody().length() > 205 ? string.lastIndexOf(" ") : string.length()))
                .map(string -> string.equals(post.getPostBody()) ? string : string.concat("..."))
                .collect(Collectors.joining());

        this.imgUrl = post.getImgUrl();
        this.createdAt = SpringWebConfig.DATE_TIME_FORMATTER.format(post.getCreatedAt());
        this.numberOfComments = post.getComments().size();
        this.category = post.getCategory().getCategoryName();
        this.username = post.getCustomUser().getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPostBodyShortened() {
        return postBodyShortened;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public String getCategory() {
        return category;
    }

    public String getUsername() {
        return username;
    }
}
