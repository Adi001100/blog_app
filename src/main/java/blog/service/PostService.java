package blog.service;

import blog.domain.Category;
import blog.domain.Post;
import blog.dto.PostDetails;
import blog.dto.PostFormDataCreate;
import blog.dto.PostFormDataUpdate;
import blog.dto.PostListItem;
import blog.exception.CategoryNotFoundByIdException;
import blog.exception.DontGivePublishTimeException;
import blog.exception.NotTheUsersPost;
import blog.exception.PostNotFoundByIdException;
import blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    private final CustomUserService customUserService;
    private final ChatGptService chatGptService;

    @Autowired
    public PostService(PostRepository postRepository, CategoryService categoryService,
                       EmailService emailService, CloudinaryService cloudinaryService,
                       CustomUserService customUserService, ChatGptService chatGptService) {
        this.postRepository = postRepository;
        this.categoryService = categoryService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
        this.customUserService = customUserService;
        this.chatGptService = chatGptService;
    }

    public PostDetails createPost(PostFormDataCreate postFormData) {
        PostDetails result = null;
        Category categoryToPost = categoryService.getCategoryById(postFormData.getCategoryId());
        if (categoryToPost != null) {
            Post postToCreate = new Post(postFormData, categoryToPost);
            if (postFormData.getImgUrl() != null) {
                postToCreate.setImgUrl(cloudinaryService.uploadPhoto(postFormData.getImgUrl()));
            } else {
                postToCreate.setImgUrl(chatGptService.generateImg(postFormData.getTitle()));
            }
            if (postFormData.getVideoUrl() != null) {
                postToCreate.setVideoUrl(cloudinaryService.uploadVideo(postFormData.getVideoUrl()));
            }
            if (postFormData.getPostBody() == null) {
                postToCreate.setPostBody(chatGptService.generate(postFormData.getTitle()));
            }
            postToCreate.setCustomUser(customUserService.findCustomUserByUsername(getLoggedInUser()));
            Post postSaved = postRepository.save(postToCreate);
            result = new PostDetails(postSaved);
            result.setCategory(categoryToPost.getCategoryName());
            result.setImgUrl(postToCreate.getImgUrl());
            result.setVideoUrl(postToCreate.getVideoUrl());
        } else {
            throw new CategoryNotFoundByIdException(postFormData.getCategoryId());
        }
        return result;
    }

    public List<PostListItem> getPostListItems() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostListItem::new)
                .collect(Collectors.toList());
    }

    public PostDetails getPostDetailsById(Long id) {
        PostDetails details = null;
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            details = new PostDetails(postRepository.getById(id));
        } else {
            throw new PostNotFoundByIdException(id);
        }
        return details;
    }

    public List<PostListItem> getPostListByCategory(String category) {
        categoryService.getCategoryDetailsByCategoryName(category);
        return postRepository.findByCategoryByOrderByCreatedAtDesc(category)
                .stream()
                .map(PostListItem::new)
                .collect(Collectors.toList());
    }

    public List<PostListItem> getPostListByUsername(String username) {
        customUserService.getCustomUserDetailsByUsername(username);
        return postRepository.findByUsernameByOrderByCreatedAtDesc(username)
                .stream()
                .map(PostListItem::new)
                .collect(Collectors.toList());
    }


    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundByIdException(id));
        if (isAdminOrPostAuthor(post)) {
            post.setIsDeleted(true);
        } else {
            throw new NotTheUsersPost();
        }
    }

    public PostDetails restorePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundByIdException(id));
        if (isAdminOrPostAuthor(post)) {
            post.setIsDeleted(false);
            post.setCreatedAt(LocalDateTime.now());
        } else {
            throw new NotTheUsersPost();
        }
        return new PostDetails(post);
    }

    public PostDetails editPost(Long id, PostFormDataUpdate postFormDataUpdate) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundByIdException(id));
        if (isAdminOrPostAuthor(post)) {
            if (postFormDataUpdate.getTitle() != null) {
                post.setTitle(postFormDataUpdate.getTitle());
            }
            if (postFormDataUpdate.getPostBody() != null) {
                post.setPostBody(postFormDataUpdate.getPostBody());
            }
            if (postFormDataUpdate.getImgUrl() != null) {
                post.setImgUrl(postFormDataUpdate.getImgUrl());
            }
            if (postFormDataUpdate.getCategoryId() != null) {
                post.setCategory(categoryService.getCategoryById(postFormDataUpdate.getCategoryId()));
            }

            if (Boolean.TRUE.equals(postFormDataUpdate.getTimedPost())) {
                if (postFormDataUpdate.getPublishTime() != null) {
                    post.setPublishTime(postFormDataUpdate.getPublishTime());
                    post.setPublished(false);
                } else if (post.getPublishTime().isAfter(LocalDateTime.now())) {
                    post.setPublishTime(post.getPublishTime());
                } else {
                    throw new DontGivePublishTimeException(post.getPublishTime());
                }
            } else if (post.getPublishTime().isAfter(LocalDateTime.now())) {
                post.setPublishTime(post.getPublishTime());
            } else {
                post.setPublishTime(LocalDateTime.now());
            }
            post.setCreatedAt(LocalDateTime.now());
        } else {
            throw new NotTheUsersPost();
        }
        return new PostDetails(post);

    }

    @Scheduled(cron = "0 * * * * *")
    public void publishScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> scheduledPosts = postRepository.findByPublishTimeBefore(now);

        for (Post post : scheduledPosts) {
            if (Boolean.FALSE.equals(post.getPublished())) {
                post.setPublished(true);
            }
        }
    }

    public UserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    private boolean isAdminOrPostAuthor(Post post) {
        UserDetails userDetails = getLoggedInUser();
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                post.getCustomUser().getUsername().equals(userDetails.getUsername());
    }

    public PostDetails likePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundByIdException(id));
        if (Boolean.TRUE.equals(post.getPublished())) {
            Integer like = post.getNumberOfLike();
            like++;
            post.setNumberOfLike(like);
            String email = post.getCustomUser().getUserEmail();
            String userName = post.getCustomUser().getUsername();
            emailService.sendEmailAboutLike(email, userName);
            PostDetails postDetails = new PostDetails(post);
            postDetails.setNumberOfLike(like);
            return postDetails;
        } else {
            throw new PostNotFoundByIdException(id);
        }
    }
}
