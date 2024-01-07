package blog.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class CustomUserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void test_loginSuccessful() throws Exception {
        saveTestUser();
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"test1234\"\n" +
                "}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("TEST")));
    }

    @Test
    void test_LoginEmailNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"testtest.test\",\n" +
                "    \"password\": \"test1234\"\n" +
                "}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("userEmail")))
                .andExpect(jsonPath("$[0].message", is("Not valid email!")));
    }

    @Test
    void test_loginPasswordNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"    \"\n" +
                "}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("password")))
                .andExpect(jsonPath("$[0].message", is("Must be not blank!")));
    }

    @Test
    void test_loginFailedWrongPassword() throws Exception {
        saveTestUser();
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"teSt1234\"\n" +
                "}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("password")))
                .andExpect(jsonPath("$[0].message", is("Wrong password, please try again!")));
    }

    @Test
    void test_loginFailedWrongEmail() throws Exception {
        saveTestUser();
        String inputCommand = "{\n" +
                "    \"userEmail\": \"tesT@test.test\",\n" +
                "    \"password\": \"test1234\"\n" +
                "}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("email")))
                .andExpect(jsonPath("$[0].message", is("User not found by email: tesT@test.test")));
    }


    @Test
    void test_registerSuccessful() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"TEST\",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("TEST")))
                .andExpect(jsonPath("$.fullName", is("Test Test")));
    }

    @Test
    void test_emailNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"testtesttest\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"TEST\",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("userEmail")))
                .andExpect(jsonPath("$[0].message", is("Not valid email!")));
    }

    @Test
    void test_passwordNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"Test\",\n" +
                "    \"username\": \"TEST\",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("password")))
                .andExpect(jsonPath("$[0].message", is("Password must be 8 or more characters in length.,Password must contain 1 or more digit characters.,Password must contain 1 or more special characters.")));
    }

    @Test
    void test_usernameNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"     \",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("username")))
                .andExpect(jsonPath("$[0].message", is("Must be not blank!")));
    }

    @Test
    void test_fullNameNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"TEST\",\n" +
                "    \"fullName\": \"    \"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("fullName")))
                .andExpect(jsonPath("$[0].message", is("Must be not blank!")));
    }

    @Test
    void test_emailAlreadyExist() throws Exception {
        saveTestUser();
        String inputCommand = "{\n" +
                "    \"userEmail\": \"test@test.test\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"Bob\",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("email")))
                .andExpect(jsonPath("$[0].message", is("Email already exists: test@test.test")));
    }

    @Test
    void test_usernameAlreadyExist() throws Exception {
        saveTestUser();
        String inputCommand = "{\n" +
                "    \"userEmail\": \"joe@joe.joe\",\n" +
                "    \"password\": \"Test1234+\",\n" +
                "    \"username\": \"TEST\",\n" +
                "    \"fullName\": \"Test Test\"\n" +
                "}";

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("username")))
                .andExpect(jsonPath("$[0].message", is("Username already exists: TEST")));
    }

    @Test
    void testgetUserDetailsWithPostNoUserFind() throws Exception {
        saveTestUser();
        saveTestCategory();
        saveTestPost();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/page/{username}", "Pete")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("username")))
                .andExpect(jsonPath("$[0].message", is("User not found by username: Pete")));
    }

    @Test
    void testgetUserDetailsWithPostUserFind() throws Exception {
        saveTestUser();
        saveTestCategory();
        saveTestPost();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/page/{username}", "TEST")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("TEST")))
                .andExpect(jsonPath("$.year", nullValue()))
                .andExpect(jsonPath("$.postDetailsList", hasSize(1)));

    }

    private void saveTestCategory() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name, creation_at, description) VALUES ('wine', '2023-08-27 11:24:48.658969', 'wine blog'); ").executeUpdate();
    }

    private void saveTestPost() {
        entityManager.createNativeQuery(
                        "INSERT INTO post (title,category_id, post_body, img_url,creation_at,user_email) VALUES ('Wine','1', 'Wine is good.'," +
                                "'http://wine.wine/wines','2023-08-27 11:24:48.658969','test@test.test'); "
                )
                .executeUpdate();
    }

    private void saveTestUser() {
        entityManager.createNativeQuery(
//                        "INSERT INTO custom_user (user_email, username,is_enabled) VALUES ('test@test.test', 'Bob',false); " +
//                                "INSERT INTO custom_user (user_email, username, is_enabled) VALUES ('bob@bob.bob', 'TEST',false); " +
                        "INSERT INTO custom_user (user_email, username, password, is_enabled) VALUES ('test@test.test', 'TEST', '$2a$10$iGKdBRBgaxfo9gUimnwkZuyk9ZDwYsVbiRoenrDbtJ3ecNv7LrqOK',false); " +
                                "INSERT INTO user_role(custom_user_user_email, roles) VALUES ('test@test.test', 'ROLE_ADMIN')"
                )
                .executeUpdate();
    }
}
