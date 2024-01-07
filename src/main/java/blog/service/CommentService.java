package blog.service;

import blog.domain.Comment;
import blog.domain.CustomUser;
import blog.domain.Post;
import blog.dto.CommentDetails;
import blog.dto.CommentFormDataCreate;
import blog.dto.CommentFormDataUpdate;
import blog.exception.CommentNotFoundByIdException;
import blog.exception.NotTheUsersComment;
import blog.exception.PostNotFoundByIdException;
import blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private CommentRepository commentRepository;
    private PostService postService;
    private EmailService emailService;
    private CloudinaryService cloudinaryService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostService postService, EmailService emailService, CloudinaryService cloudinaryService, CloudinaryService cloudinaryService1) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService1;
    }

    public CommentDetails createComment(CommentFormDataCreate commentFormData) {
        CommentDetails result;
        Post postToComment = postService.getPostById(commentFormData.getPostId());
        if (postToComment != null && postToComment.getPublished()) {
            CustomUser customUser = postToComment.getCustomUser();
            Comment commentToCreate = new Comment(commentFormData, postToComment);
            String email = customUser.getUserEmail();
            String userName = customUser.getUsername();
            emailService.sendEmailAboutComment(email, userName);
            if (commentFormData.getImgUrl() != null) {
                commentToCreate.setImgUrl(cloudinaryService.uploadPhoto(commentFormData.getImgUrl()));
            }
            commentToCreate.setAuthor(getLoggedInUser().getUsername());
            Comment commentSaved = commentRepository.save(commentToCreate);
            result = new CommentDetails(commentSaved);
        } else {
            throw new PostNotFoundByIdException(commentFormData.getPostId());
        }
        return result;
    }

    public List<CommentDetails> getCommentList() {
        return commentRepository.findAll()
                .stream()
                .map(CommentDetails::new)
                .collect(Collectors.toList());
    }

    public CommentDetails getCommentDetailsById(Long id) {
        CommentDetails details = null;
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment != null) {
            details = new CommentDetails(commentRepository.getOne(id));
        } else {
            throw new CommentNotFoundByIdException(id);
        }
        return details;
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundByIdException(id));
        if (isAdminOrCommentAuthor(comment)) {
            commentRepository.delete(comment);
        } else
            throw new NotTheUsersComment();
    }

    public CommentDetails editComment(Long id, CommentFormDataUpdate commentFormDataUpdate) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundByIdException(id));
        if (isAdminOrCommentAuthor(comment)) {
            if (commentFormDataUpdate.getPostId() != null) {
                Post postToComment = postService.getPostById(commentFormDataUpdate.getPostId());
                if (postToComment != null) {
                    comment.setPost(postToComment);
                } else {
                    throw new PostNotFoundByIdException(commentFormDataUpdate.getPostId());
                }
            }
            if (commentFormDataUpdate.getAuthor() != null) {
                comment.setAuthor(commentFormDataUpdate.getAuthor());
            }
            if (commentFormDataUpdate.getCommentBody() != null) {
                comment.setCommentBody(commentFormDataUpdate.getCommentBody());
            }
        } else {
            throw new NotTheUsersComment();
        }
        return new CommentDetails(comment);
    }

    public UserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    private boolean isAdminOrCommentAuthor(Comment comment) {
        UserDetails userDetails = getLoggedInUser();
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                comment.getAuthor().equals(userDetails.getUsername());
    }
}
