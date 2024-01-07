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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@WithMockUser(username = "Test", authorities = {"ROLE_ADMIN"})
public class CategoryControllerIt {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void test_saveSuccessful() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": \"book\",\n" +
                "    \"description\": \"book blog\"\n" +
                "}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "Test", authorities = {"ROLE_GUEST"})
    void test_saveFailedWrongRole() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": \"book\",\n" +
                "    \"description\": \"book blog\"\n" +
                "}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void test_CategoryNameNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": \"     \",\n" +
                "     \"description\": \"book blog\"\n" +
                "}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("categoryName")))
                .andExpect(jsonPath("$[0].message", is("Must not be blank!")));
    }

    @Test
    void test_CategoryAlreadyExists() throws Exception {
        saveTestCategory();
        String inputCommand = "{\n" +
                "    \"categoryName\": \"book\",\n" +
                "    \"description\": \"book blog\"\n" +
                "}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("categoryName")))
                .andExpect(jsonPath("$[0].message", is("Category already exist: book")));
    }


    @Test
    void test_atStart_emptyList() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void test_listSuccessful() throws Exception {
        saveTestCategory();
        mockMvc.perform(get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].categoryName", is("book")))
                .andExpect(jsonPath("$[0].createdAt", is("2023-08-27 11:24:48")));

    }

    @Test
    void test_getCategoryByIdSuccessful() throws Exception {
        saveTestCategory();
        mockMvc.perform(get("/api/categories/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("book")))
                .andExpect(jsonPath("$.description", is("book blog")))
                .andExpect(jsonPath("$.createdAt", is("2023-08-27 11:24:48")));

    }

    @Test
    void test_getCategoryByIdFailedIdNotFound() throws Exception {
        saveTestCategory();
        mockMvc.perform(get("/api/categories/9")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("id")))
                .andExpect(jsonPath("$[0].message", is("Category not found by id: 9")));

    }

    private void saveTestCategory() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name, creation_at, description) VALUES ('book', '2023-08-27 11:24:48.658969', 'book blog'); ").executeUpdate();
    }


}
