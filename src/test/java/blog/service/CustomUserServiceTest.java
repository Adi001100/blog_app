package blog.service;

import blog.config.UserRole;
import blog.domain.CustomUser;
import blog.dto.*;
import blog.exception.*;
import blog.repository.ConfirmationRepository;
import blog.repository.CustomUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @Mock
    private LoginFormData loginFormData;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserFormData customUserFormData;

    @Mock
    private ConfirmationRepository confirmationRepository;

    @Mock
    private EmailService emailService;


    @InjectMocks
    private CustomUserService customUserService;

    private final CustomUser customUser = new CustomUser();

    @Test
    void test_loadUserByUserByEmailSuccessful() {
        customUser.setUsername("JOE");
        customUser.setPassword("TEST");
        customUser.setRoles(List.of(UserRole.ROLE_ADMIN));
        when(customUserRepository.findCustomUserByUserEmail("joe@joe.joe")).thenReturn(customUser);
        UserDetails userDetails = customUserService.loadUserByUsername("joe@joe.joe");
        assertEquals("JOE", userDetails.getUsername());
    }

    @Test
    void test_loadUserByUserByEmailFailedNotFoundUser() {
        when(customUserRepository.findCustomUserByUserEmail("joe@joe.joe")).thenReturn(null);
        assertThrows(UserNotFoundByEmailException.class, () -> customUserService.loadUserByUsername("joe@joe.joe"));
    }

    @Test
    void test_loginSuccessful() {
        customUser.setUsername("TEST");
        customUser.setPassword("$2a$10$iGKdBRBgaxfo9gUimnwkZuyk9ZDwYsVbiRoenrDbtJ3ecNv7LrqOK");
        customUser.setRoles(List.of(UserRole.ROLE_ADMIN));
        when(loginFormData.getPassword()).thenReturn("test1234");
        when(loginFormData.getUserEmail()).thenReturn("test@test.test");
        when(customUserRepository.findCustomUserByUserEmail("test@test.test")).thenReturn(customUser);
        when(passwordEncoder.matches("test1234", "$2a$10$iGKdBRBgaxfo9gUimnwkZuyk9ZDwYsVbiRoenrDbtJ3ecNv7LrqOK")).thenReturn(true);
        AuthenticatedUserInfo userDetails = customUserService.login(loginFormData);
        assertEquals("TEST", userDetails.getUsername());
    }

    @Test
    void test_loginFailedWrongPassword() {
        customUser.setUsername("TEST");
        customUser.setPassword("test1234");
        customUser.setRoles(List.of(UserRole.ROLE_ADMIN));
        when(loginFormData.getPassword()).thenReturn("test1234");
        when(loginFormData.getUserEmail()).thenReturn("test@test.test");
        when(customUserRepository.findCustomUserByUserEmail("test@test.test")).thenReturn(customUser);
        assertThrows(WrongPasswordException.class, () -> customUserService.login(loginFormData));
    }

    @Test
    void testGetUserDetailsWithPostUserFind() {
        customUser.setUsername("JOE");
        customUser.setPassword("TEST");
        when(customUserRepository.findCustomUserByUsername("JOE")).thenReturn(customUser);
        CustomUserPage customUserPage = customUserService.getUserDetailsWithPost("JOE");

        assertEquals("JOE", customUserPage.getUserName());
        assertEquals(0, customUserPage.getPostDetailsList().size());
        assertNotNull(customUserPage.getPostDetailsList());
        assertTrue(customUserPage.getPostDetailsList().isEmpty());
    }

    @Test
    void testGetUserDetailsWithPostUserNotFind() {
        customUser.setUsername("JOE");
        customUser.setPassword("TEST");
        when(customUserRepository.findCustomUserByUsername("JOE")).thenReturn(null);
        assertThrows(UserNotFoundByNameException.class, () -> customUserService.getUserDetailsWithPost("JOE"));
    }

    @Test
    void test_registerUserSuccessful() {
        customUser.setUserEmail("blogmastersget@gmail.com");
        customUser.setUsername("Bob");
        when(customUserFormData.getUserEmail()).thenReturn("blogmastersget@gmail.com");
        when(customUserFormData.getUsername()).thenReturn("Bob");
        when(passwordEncoder.encode(customUserFormData.getPassword())).thenReturn("$2a$10$iGKdBRBgaxfo9gUimnwkZuyk9ZDwYsVbiRoenrDbtJ3ecNv7LrqOK");
        when(customUserRepository.save(any())).thenReturn(customUser);


        CustomUserDetails details = customUserService.register(customUserFormData);
        assertEquals(customUser.getUsername(), details.getUsername());
        assertEquals(customUser.getFullName(), details.getFullName());
    }

    @Test
    void test_registerUserEmailAlreadyExists() {
        when(customUserRepository.findAll()).thenReturn(createUserList());
        when(customUserFormData.getUserEmail()).thenReturn("joe@joe.joe");
        assertThrows(EmailAlreadyExistsException.class, () -> customUserService.register(customUserFormData));
    }

    @Test
    void test_findCustomUserByUsername() {
        String username = "tesztUser";
        CustomUser expectedUser = new CustomUser();
        expectedUser.setUsername("tesztUser");
        UserDetails applicationUser = mock(UserDetails.class);
        when(customUserRepository.findCustomUserByUsername(username)).thenReturn(expectedUser);
        when(applicationUser.getUsername()).thenReturn("tesztUser");
        CustomUser result = customUserService.findCustomUserByUsername(applicationUser);
        assertEquals(expectedUser.getUsername(), result.getUsername());
    }

    @Test
    void test_registerUsernameAlreadyExists() {
        when(customUserRepository.findAll()).thenReturn(createUserList());
        when(customUserFormData.getUsername()).thenReturn("Bob");
        assertThrows(UsernameAlreadyExistsException.class, () -> customUserService.register(customUserFormData));
    }

    @Test
    void test_getCustomUserDetailsByUsernameSuccess() {
        customUser.setUsername("Test");
        when(customUserRepository.findCustomUserByUsername("Test")).thenReturn(customUser);
        assertEquals(customUser.getUsername(), customUserService.getCustomUserDetailsByUsername("Test").getUsername());
    }

    @Test
    void test_getCustomUserDetailsByUsernameFailed() {
        when(customUserRepository.findCustomUserByUsername("Test")).thenReturn(null);
        assertThrows(UserNotFoundByNameException.class, () -> customUserService.getCustomUserDetailsByUsername("Test"));
    }

    List<CustomUser> createUserList() {
        CustomUser customUser1 = new CustomUser();
        CustomUser customUser2 = new CustomUser();
        customUser1.setUserEmail("joe@joe.joe");
        customUser1.setPassword("test1234");
        customUser1.setUsername("Joe");
        customUser1.setFullName("Little Joe");
        customUser2.setUserEmail("bob@bob.bob");
        customUser2.setPassword("test1234");
        customUser2.setUsername("Bob");
        customUser2.setFullName("Big Bob");
        return List.of(customUser1, customUser2);
    }
}
