package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.utils.TestUtilsIT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.UserController.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
@Testcontainers
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("dbname")
            .withUsername("sa")
            .withPassword("sa")
            .withInitScript("script.sql");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    @Test
    void registrationUserWithCorrectData() throws Exception {
        AuthRegistrationRequest user = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtilsIT.writeJson(user))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isCreated());
        database.getTestQueryString();
//        assertThat(userRepository.findAll().size()).isGreaterThan(0);
//        assertThat(userRepository.findByUsername(user.getUsername())).isNotNull();
//        assertThat(userRepository.findByUsername(user.getUsername()).get().getPassword())
//                .isNotEqualTo(user.getPassword());
    }
//
//    @Test
//    void registrationUserWithIncorrectData() throws Exception {
//        AuthRegistrationRequest user = TestUtilsIT.readJson(
//                TestUtilsIT.readFixture("users/sampleUser.json"),
//                new TypeReference<>() {
//                }
//        );
//
//        user.setConfirmPassword("123412414");
//
//        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
//                .content(TestUtilsIT.writeJson(user))
//                .contentType(APPLICATION_JSON);
//
//        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);
//
//        resultActions.andExpect(status().isUnprocessableEntity());
//        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
//        AppError appError = TestUtilsIT.readJson(content, new TypeReference<>() {});
//
//        assertThat(userRepository.findAll().size()).isEqualTo(0);
//        assertThat(userRepository.findByUsername(user.getUsername())).isEmpty();;
//        assertThat(appError.getMessage()).contains("Пароль и потверждающие пароль не совпадают");
//        assertThat(appError.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
//    }
//
//    @Test
//    void registrationUserWithExistedUsername() throws Exception {
//        AuthRegistrationRequest user = TestUtilsIT.readJson(
//                TestUtilsIT.readFixture("users/sampleUser.json"),
//                new TypeReference<>() {
//                }
//        );
//
//        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
//                .content(TestUtilsIT.writeJson(user))
//                .contentType(APPLICATION_JSON);
//
//        testUtilsIT.performUnsecuredRequest(request);
//
//        MockHttpServletRequestBuilder sameRequest = post(USER_CONTROLLER_PATH)
//                .content(TestUtilsIT.writeJson(user))
//                .contentType(APPLICATION_JSON);
//
//        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);
//
//        resultActions.andExpect(status().isUnprocessableEntity());
//        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
//        AppError appError = TestUtilsIT.readJson(content, new TypeReference<>() {});
//
//        assertThat(userRepository.findAll().size()).isEqualTo(1);
//        assertThat(userRepository.findByUsername(user.getUsername())).isPresent();
//        assertThat(appError.getMessage()).contains("Пользователь с таким именем уже существует");
//        assertThat(appError.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
//    }
}
