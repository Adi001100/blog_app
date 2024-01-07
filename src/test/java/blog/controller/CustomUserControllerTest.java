package blog.controller;

import blog.domain.CustomUser;
import blog.domain.HttpResponse;
import blog.dto.*;
import blog.service.CustomUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserControllerTest {

    @Mock
    private CustomUserService customUserService;

    @InjectMocks
    private CustomUserController customUserController;

    private final CustomUser customUser = new CustomUser();

    @Test
    void test_registration() {
        CustomUserFormData customUserFormData = new CustomUserFormData();
        CustomUserDetails customUserDetails = new CustomUserDetails(customUser);
        when(customUserService.register(customUserFormData)).thenReturn(customUserDetails);
        ResponseEntity<CustomUserDetails> response = customUserController.registration(customUserFormData);
        verify(customUserService, times(1)).register(customUserFormData);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_login() {
        LoginFormData loginFormData = new LoginFormData();
        UserDetails applicationUser = mock(UserDetails.class);
        AuthenticatedUserInfo authenticatedUserInfo = new AuthenticatedUserInfo(applicationUser);
        when(customUserService.login(loginFormData)).thenReturn(authenticatedUserInfo);
        ResponseEntity<AuthenticatedUserInfo> response = customUserController.login(loginFormData);
        verify(customUserService, times(1)).login(loginFormData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getUserDetailsWithPost() {
        String username = "testuser";
        CustomUserPage customUserPage = new CustomUserPage(customUser);
        when(customUserService.getUserDetailsWithPost(username)).thenReturn(customUserPage);
        ResponseEntity<CustomUserPage> response = customUserController.getUserDetailsWithPost(username);
        verify(customUserService, times(1)).getUserDetailsWithPost(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_confirmUserAccount() {
        String token = "testtoken";
        when(customUserService.verifyToken(token)).thenReturn(true);
        ResponseEntity<HttpResponse> response = customUserController.confirmUserAccount(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
