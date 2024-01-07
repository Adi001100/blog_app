package blog.service;

import blog.config.UserRole;
import blog.domain.Confirmation;
import blog.domain.CustomUser;
import blog.domain.Post;
import blog.dto.*;
import blog.exception.*;
import blog.repository.ConfirmationRepository;
import blog.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomUserService implements UserDetailsService {

    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;

    @Autowired
    public CustomUserService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder, ConfirmationRepository confirmationRepository, EmailService emailService) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationRepository = confirmationRepository;

        this.emailService = emailService;
    }

    public CustomUserDetails register(CustomUserFormData data) {
        if (checkEmail(data)) {
            throw new EmailAlreadyExistsException(data.getUserEmail());
        }
        if (checkUsername(data)) {
            throw new UsernameAlreadyExistsException(data.getUsername());
        }
        CustomUser customUserToRegister = new CustomUser(data);
        customUserToRegister.setPassword(passwordEncoder.encode(data.getPassword()));
        CustomUser customUserRegistered = customUserRepository.save(customUserToRegister);

        Confirmation confirmation = new Confirmation(customUserRegistered);
        confirmationRepository.save(confirmation);
        emailService.sendValidationEmail(customUserRegistered.getFullName(), customUserRegistered.getUserEmail(), confirmation.getToken());

        return new CustomUserDetails(customUserRegistered);
    }

    private List<CustomUser> getUserList() {
        return customUserRepository.findAll();
    }

    private boolean checkEmail(CustomUserFormData data) {
        boolean result = false;
        List<CustomUser> customUsers = getUserList();
        for (CustomUser customUser : customUsers) {
            if (customUser.getUserEmail().equals(data.getUserEmail())) {
                result = true;
            }
        }
        return result;
    }

    private boolean checkUsername(CustomUserFormData data) {
        boolean result = false;
        List<CustomUser> customUsers = getUserList();
        if (!customUsers.isEmpty()) {
            for (CustomUser customUser : customUsers) {
                if (customUser.getUsername().equals(data.getUsername())) {
                    result = true;
                }
            }
        }
        return result;
    }

    public AuthenticatedUserInfo login(LoginFormData data) {
        UserDetails user = loadUserByUsername(data.getUserEmail());
        if (!passwordEncoder.matches(data.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }
        return new AuthenticatedUserInfo(user);
    }

    public CustomUserPage getUserDetailsWithPost(String username) {
        CustomUser customUser = customUserRepository.findCustomUserByUsername(username);
        if (customUser == null) {
            throw new UserNotFoundByNameException(username);
        }
        CustomUserPage customUserPage = new CustomUserPage(customUser);
        List<PostDetails> postDetailsList = new ArrayList<>();
        if (customUser.getPostList() != null) {
            for (Post post : customUser.getPostList()) {
                postDetailsList.add(new PostDetails(post));
            }
            customUserPage.setPostDetailsList(postDetailsList);
        }
        return customUserPage;
    }

    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        CustomUser user = customUserRepository.findCustomUserByUserEmail(confirmation.getCustomUser().getUserEmail());
        user.setEnabled(true);
        user.setRoles(List.of(UserRole.ROLE_USER));
        confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkVerificationEmail() {

        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(5);
        List<CustomUser> customUsers = customUserRepository.findCustomUserByEnabled(localDateTime);
        for (CustomUser customUser : customUsers) {
            if (!customUser.isEnabled()) {
                Confirmation confirmation = confirmationRepository.findByCustomUser_UserEmail(customUser.getUserEmail());
                emailService.sendEmailWrongValidation(customUser.getUsername(), customUser.getUserEmail());
                confirmationRepository.delete(confirmation);
                customUserRepository.delete(customUser);

            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        CustomUser customUser = customUserRepository.findCustomUserByUserEmail(email);
        if (customUser == null) {
            throw new UserNotFoundByEmailException(email);
        }
        String[] roles = customUser.getRoles().stream()
                .map(Enum::toString)
                .toArray(String[]::new);

        return User
                .withUsername(customUser.getUsername())
                .authorities(AuthorityUtils.createAuthorityList(roles))
                .password(customUser.getPassword())
                .build();
    }

    public CustomUser findCustomUserByUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return customUserRepository.findCustomUserByUsername(username);
    }

    public CustomUserDetails getCustomUserDetailsByUsername(String username) {
        CustomUserDetails details = null;
        CustomUser customUser = customUserRepository.findCustomUserByUsername(username);
        if (customUser != null) {
            details = new CustomUserDetails(customUser);
        } else {
            throw new UserNotFoundByNameException(username);
        }
        return details;
    }
}
