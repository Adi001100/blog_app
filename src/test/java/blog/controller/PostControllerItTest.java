package blog.controller;

import blog.domain.Post;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@WithMockUser(authorities = {"ROLE_ADMIN"})
class PostControllerItTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    private final String IMG_URL_TO_UPLOAD = "src/test/resources/img-YrKQpDoMLBI40UyCBLqTVFed.png";

    private final String IMG_URL_FROM_CLOUDINARY = "https://res.cloudinary.com/dpksvz4fb/image/upload/v1708345106/img-YrKQpDoMLBI40UyCBLqTVFed.png";

    private final String VIDEO_URL_TO_UPLOAD = "src/test/resources/video.mp4";

    private final String VIDEO_URL_FROM_CLOUDINARY = "https://res.cloudinary.com/dpksvz4fb/video/upload/v1708345502/video.mp4";

    @Test
    void test_saveSuccessfulWithImg() throws Exception {
        saveCategory();
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"imgUrl\": \"" + IMG_URL_TO_UPLOAD + "\", \n" +
                "    \"categoryId\": \"1\",\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("test")))
                .andExpect(jsonPath("$.postBody", is("book blog")))
                .andExpect(jsonPath("$.imgUrl", is(IMG_URL_FROM_CLOUDINARY)))
                .andExpect(jsonPath("$.category", is("book")));
    }

    @Test
    void test_saveSuccessfulWithoutImg() throws Exception {
        saveCategory();
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"categoryId\": \"1\",\n" +
                "    \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("test")))
                .andExpect(jsonPath("$.postBody", is("book blog")))
                .andExpect(jsonPath("$.imgUrl", notNullValue()))
                .andExpect(jsonPath("$.category", is("book")));
    }

    @Test
    void test_saveSuccessfulWithVideo() throws Exception {
        saveCategory();
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"videoUrl\": \"" + VIDEO_URL_TO_UPLOAD + "\", \n" +
                "    \"categoryId\": \"1\",\n" +
                "    \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("test")))
                .andExpect(jsonPath("$.postBody", is("book blog")))
                .andExpect(jsonPath("$.videoUrl", is(VIDEO_URL_FROM_CLOUDINARY)))
                .andExpect(jsonPath("$.category", is("book")));
    }

    @Test
    @WithMockUser(username = "Test", authorities = {"ROLE_GUEST"})
    void test_saveFailedWrongRole() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"imgUrl\": \"" + IMG_URL_TO_UPLOAD + "\", \n" +
                "    \"categoryId\": \"1\",\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void test_NotFindCategoryId() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String inputCommand = "{\n" +
                "    \"title\": \"Book lover\",\n" +
                "    \"postBody\": \"Love reading\",\n" +
                "    \"imgUrl\": \"http://test.test/test\",\n" +
                "    \"categoryId\": " + 2 + " ,\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "TeSt", authorities = {"ROLE_USER"})
    void test_deleteSuccessful() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        Post beforeDeletPost = entityManager.find(Post.class, 1L);
        assertNotNull(beforeDeletPost);
        assertFalse(beforeDeletPost.getIsDeleted());
        mockMvc.perform(delete("/api/posts/delete/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
        Post afterDeletePost = entityManager.find(Post.class, 1L);
        assertNotNull(afterDeletePost);
        assertTrue(afterDeletePost.getIsDeleted());
    }

    @Test
    void test_postNotExists() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        mockMvc.perform(delete("/api/posts/delete/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("id")))
                .andExpect(jsonPath("$[0].message", is("Post not found by id: 2")));

    }


    @Test
    @WithMockUser(username = "TeSt", authorities = {"ROLE_USER"})
    void test_restoreSuccessful() throws Exception {
        saveCategory();
        saveCustomUser();
        savePostTorestore();
        Post beforeDeletPost = entityManager.find(Post.class, 1L);
        assertNotNull(beforeDeletPost);
        assertTrue(beforeDeletPost.getIsDeleted());
        mockMvc.perform(put("/api/posts/restore/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        Post afterDeletePost = entityManager.find(Post.class, 1L);
        assertNotNull(afterDeletePost);
        assertFalse(afterDeletePost.getIsDeleted());
    }


    @Test
    void test_postNotFound() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String newPostCommand = "{\n" +
                "    \"title\": \"power boat\",\n" +
                "    \"postBody\": \"real life is life on the water with new powerboat\",\n" +
                "    \"imgUrl\": \"http://powerboat.pictures\",\n" +
                "    \"categoryId\":" + 1 + " ,\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                " }";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/edit/{id}", "2")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(newPostCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message", is("Post not found by id: 2")));

    }

    @Test
    void test_postBeforeEdit() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.postBody", is("the real life is life on the water")));
    }


    @Test
    @WithMockUser(username = "TeSt", authorities = {"ROLE_USER"})
    void test_editNoTimedPost() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        String newPostCommand = "{\n" +
                "    \"title\": \"power boat\",\n" +
                "    \"postBody\": \"real life is life on the water with new powerboat\",\n" +
                "    \"imgUrl\": \"http://powerboat.pictures\",\n" +
                "    \"categoryId\":" + 1 + " \n" +
                " }";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/edit/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(newPostCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title", is("power boat")))
                .andExpect(jsonPath("$.postBody", is("real life is life on the water with new powerboat")));

    }

    @Test
    void test_editNoTimedPostChangeTimedPostFailed() throws Exception {
        saveCategory();
        saveTestPost();
        String newPostCommand = "{\n" +
                "    \"title\": \"power boat\",\n" +
                "    \"postBody\": \"real life is life on the water with new powerboat\",\n" +
                "    \"imgUrl\": \"http://powerboat.pictures\",\n" +
                "    \"timedPost\": \"true\",\n" +
                "    \"categoryId\":" + 1 + " ,\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                " }";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/edit/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(newPostCommand)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message", is("The old publish time has passed: 2023-08-27T11:24:48.658969 and the new publish time is null!")));

    }

    @Test
    void test_saveFailedWrongImgUrl() throws Exception {
        saveCategory();
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"imgUrl\": \"/Users/huszaradam/Downloads/20140807foto\", \n" +
                "    \"categoryId\": \"1\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("photo")))
                .andExpect(jsonPath("$[0].message", is("Photo upload failed, wrong URL!")));
    }

    @Test
    void test_getPostListByCategory() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        mockMvc.perform(get("/api/posts/category").param("category", "book")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void test_PostListByCategoryFailed() throws Exception {
        mockMvc.perform(get("/api/posts/category").param("category", "cook")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("categoryName")))
                .andExpect(jsonPath("$[0].message", is("Category not found by category name: cook")));
    }

    @Test
    void test_getPostListByUsername() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        mockMvc.perform(get("/api/posts/username").param("username", "TeSt")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void test_PostListByUsernameFailed() throws Exception {
        mockMvc.perform(get("/api/posts/username").param("username", "cook")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("username")))
                .andExpect(jsonPath("$[0].message", is("User not found by username: cook")));
    }

    @Test
    @WithMockUser(username = "TeSt", authorities = {"ROLE_USER"})
    void test_titleNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"  \",\n" +
                "    \"postBody\": \"book blog\", \n" +
                "    \"imgUrl\": \"/Users/huszaradam/Downloads/20140807foto\", \n" +
                "    \"categoryId\": \"1\",\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("title")))
                .andExpect(jsonPath("$[0].message", is("Must not be blank!")));
    }

    @Test
    void test_postBodyNull() throws Exception {
        saveCategory();
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"imgUrl\": \"" + IMG_URL_TO_UPLOAD + "\", \n" +
                "    \"categoryId\":" + 1 + " ,\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("test")))
                .andExpect(jsonPath("$.postBody", notNullValue()))
                .andExpect(jsonPath("$.category", is("book")));
    }

    @Test
    void test_categoryIdNotValid() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"test\",\n" +
                "    \"postBody\": \"book book\", \n" +
                "    \"imgUrl\": \"/Users/huszaradam/Downloads/20140807foto\", \n" +
                "    \"categoryId\": \"\",\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("categoryId")))
                .andExpect(jsonPath("$[0].message", is("Must not be null!")));
    }

    @Test
    void test_theNumberOfLikesOfPostIsInitiallyZero() throws Exception {
        saveCategory();
        String newPostCommand = "{\n" +
                "    \"title\": \"power boat\",\n" +
                "    \"postBody\": \"real life is life on the water with new powerboat\",\n" +
                "    \"timedPost\": \"true\",\n" +
                "    \"categoryId\":" + 1 + " ,\n" +
                " \"customUserEmail\" : \"blogmastersget@gmail.com\"\n" +
                " }";
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(newPostCommand))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numberOfLike", is(0)));

    }

    @Test
    void test_theNumberOfLikesOfPostOneAfterLike() throws Exception {
        saveCategory();
        saveCustomUser();
        savePost();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/like/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfLike", is(1)));
    }


    private void savePostTorestore() {
        entityManager.createNativeQuery("INSERT INTO  post(title,post_body, img_url,creation_at, category_id, is_deleted, user_email, published)" +
                        " values ('power boat','the real life is life on the water'," +
                        "'http://powerboat.pictures','2023-08-27 11:24:48.658969',1, true, 'test@test.test', true);")
                .executeUpdate();
    }

    private void savePost() {
        entityManager.createNativeQuery("INSERT INTO  post(title,post_body, img_url,creation_at, category_id, is_deleted,number_of_like, user_email, publish_time, published)" +
                        " values ('power boat','the real life is life on the water'," +
                        "'http://powerboat.pictures','2023-08-27 11:24:48.658969',1 , false,0, 'test@test.test', '2023-08-27 11:24:48.658969', true);")
                .executeUpdate();
    }

    private void saveCategory() {
        entityManager.createNativeQuery("INSERT INTO category (category_name,creation_at, description) " +
                        "VALUES ('book','2023-08-27 11:24:48.658969', 'book blog');")
                .executeUpdate();
    }

    private void saveCustomUser() {
        entityManager.createNativeQuery("INSERT INTO custom_user (user_email, full_name, username,is_enabled) " +
                        "VALUES ('test@test.test','TEST TEST', 'TeSt',false);")
                .executeUpdate();
    }

    private void saveTestPost() {
        entityManager.createNativeQuery(
                        "INSERT INTO post (title, post_body, img_url, is_deleted, publish_time, published) VALUES ('Wine', 'Wine is good.'," +
                                "'http://wine.wine/wines', 'false', '2023-08-27 11:24:48.658969', true); "
                )
                .executeUpdate();
    }

}
