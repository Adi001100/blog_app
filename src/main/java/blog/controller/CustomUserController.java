package blog.controller;

import blog.domain.HttpResponse;
import blog.dto.*;
import blog.service.CustomUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Configuration
@ComponentScan
@RequiredArgsConstructor
public class CustomUserController {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserController.class);

    private CustomUserService customUserService;

    @Autowired
    public CustomUserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register new User.")
    @ApiResponse(responseCode = "201", description = "New User has been created.")
    public ResponseEntity<CustomUserDetails> registration(@Valid @RequestBody CustomUserFormData customUserFormData) {
        logger.info("Http request POST api/users/signup, body:" + customUserFormData.toString());
        CustomUserDetails customUserDetails = customUserService.register(customUserFormData);
        logger.info("New user has been registered!");
        return new ResponseEntity<>(customUserDetails, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    @ApiResponse(responseCode = "200", description = "User has been logged in.")
    public ResponseEntity<AuthenticatedUserInfo> login(@Valid @RequestBody LoginFormData loginFormData) {
        logger.info("Http request POST api/users/login, body:" + loginFormData.toString());
        AuthenticatedUserInfo userDetails = customUserService.login(loginFormData);
        logger.info("User has been logged");
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @GetMapping("/page/{username}")
    @Operation(summary = "Find User with username and post(s).")
    @ApiResponse(responseCode = "200", description = "User has been found.")
    public ResponseEntity<CustomUserPage> getUserDetailsWithPost(@PathVariable("username") String username) {
        logger.info("Http request GET api/users/page with variable: " + username);
        CustomUserPage customUserPage = customUserService.getUserDetailsWithPost(username);
        logger.info("User has been found by username: " + username);
        return new ResponseEntity<>(customUserPage, HttpStatus.OK);
    }

    @GetMapping("/{token}")
    @Operation(summary = " Confirm User Account")
    @ApiResponse(responseCode = "200", description = "Account verified")
    public ResponseEntity<HttpResponse> confirmUserAccount(@PathVariable("token") String token) {
        Boolean isSuccess = customUserService.verifyToken(token);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("Success", isSuccess))
                        .message("Account verified")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
}
