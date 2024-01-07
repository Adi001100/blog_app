package blog.controller;

import blog.domain.Comment;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@WithMockUser(authorities = {"ROLE_ADMIN"})
public class CommentControllerIt {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void test_saveSuccessfulWithoutImg() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String inputCommand = "{\n" +
                "    \"postId\":" + 1 + ",\n" +
                "    \"author\": \"me\",\n" +
                "    \"commentBody\": \"test\"\n" +
                "}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "Test", authorities = {"ROLE_GUEST"})
    void test_saveFailedWrongRole() throws Exception {
        String inputCommand = "{\n" +
                "    \"postId\":" + 1 + ",\n" +
                "    \"author\": \"me\",\n" +
                "    \"commentBody\": \"test\"\n" +
                "}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void test_saveSuccesfulWithImg() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String inputCommand = "{\n" +
                "    \"postId\":" + 1 + ",\n" +
                "    \"author\": \"me\",\n" +
                "    \"imgUrl\": \"src/test/resources/dock.jpeg\",\n" +
                "    \"commentBody\": \"test\"\n" +
                "}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_NotFindPostById() throws Exception {
        String inputCommand = "{\n" +
                "    \"postId\":" + 1 + ",\n" +
                "    \"author\": \"me\",\n" +
                "    \"commentBody\": \"test\"\n" +
                "}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "me", authorities = "ROLE_USER")
    void test_deleteSuccessful() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        saveTestComment();
        Comment beforeDeletComment = entityManager.find(Comment.class, 1L);
        assertNotNull(beforeDeletComment);
        mockMvc.perform(delete("/api/comments/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
        Comment afterDeleteComment = entityManager.find(Comment.class, 1L);
        assertNull(afterDeleteComment);
    }


    @Test
    void test_commentNotExists() throws Exception {
        mockMvc.perform(delete("/api/comments/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("id")))
                .andExpect(jsonPath("$[0].message", is("Comment not found by id: 2")));

    }

    @Test
    @WithMockUser(username = "me", authorities = "ROLE_USER")
    void test_editCommentSuccessful() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        saveTestComment();
        String newCommentCommand = "{\n" +
                "\"author\": \"you\"\n" +
                "}";
        mockMvc.perform(put("/api/comments/edit/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(newCommentCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.author", is("you")));

    }

    @Test
    void test_editCommentFailedNotFoundCommentById() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String newCommentCommand = "{\n" +
                "\"author\": \"you\"\n" +
                "}";
        mockMvc.perform(put("/api/comments/edit/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(newCommentCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("id")))
                .andExpect(jsonPath("$[0].message", is("Comment not found by id: 1")));

    }

    @Test
    @WithMockUser(username = "me", authorities = "ROLE_USER")
    void test_editCommentFailedNotFoundPostById() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        saveTestComment();
        String newCommentCommand = "{\n" +
                "    \"postId\":" + 2 + "\n" +
                "}";
        mockMvc.perform(put("/api/comments/edit/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(newCommentCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("id")))
                .andExpect(jsonPath("$[0].message", is("Post not found by id: 2")));

    }


    private void saveCategory() {
        entityManager.createNativeQuery("INSERT INTO category (category_name,creation_at, description) " +
                        "VALUES ('book','2023-08-27 11:24:48.658969', 'book blog');")
                .executeUpdate();
    }

    private void savePost() {
        entityManager.createNativeQuery("INSERT INTO  post(title, user_email, post_body, img_url,creation_at, category_id, is_deleted, published)" +
                        " values ('power boat','blogmastersget@gmail.com','the real life is life on the water'," +
                        "'http://powerboat.pictures','2023-08-27 11:24:48.658969',1, true, true);")
                .executeUpdate();
    }

    private void saveTestComment() {
        entityManager.createNativeQuery("INSERT INTO comment(author, comment_body, created_at, post_id)" +
                        "values ('me', 'test', '2023-09-15 13:03:38.895629', 1);")
                .executeUpdate();
    }

    private void saveCustomUser() {
        entityManager.createNativeQuery("INSERT INTO custom_user (user_email,full_name, password,username,date_of_birth, is_enabled) " +
                "values ('blogmastersget@gmail.com','Big Bob','test1234', 'Bob', 2020 , false) ;").executeUpdate();
    }
}
