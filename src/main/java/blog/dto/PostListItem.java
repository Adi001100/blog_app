package blog.dto;

import blog.config.SpringWebConfig;
import blog.domain.Post;
import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class PostListItem {

    private final Long id;
    private final String title;
    private final String postBodyShortened;
    private final String imgUrl;
    private final String createdAt;
    private final Integer numberOfComments;
    private final String category;
    private final String username;

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
}
