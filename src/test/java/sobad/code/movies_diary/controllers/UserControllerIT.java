package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.user.UserDtoResponse;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtilsIT;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.UserController.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class UserControllerIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtilsIT testUtilsIT;

    @Test
    @Transactional
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
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        UserDtoResponse response = TestUtilsIT.readJson(content, new TypeReference<>(){});

        assertThat(userRepository.findAll().size()).isGreaterThan(0);
        assertThat(userRepository.findByUsername(user.getUsername())).isNotNull();
        assertThat(userRepository.findByUsername(user.getUsername()).get().getPassword())
                .isNotEqualTo(user.getPassword());
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @Rollback
    void registrationUserWithExistedUsername() throws Exception {
        AuthRegistrationRequest user = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );


        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtilsIT.writeJson(user))
                .contentType(APPLICATION_JSON);

        testUtilsIT.performUnsecuredRequest(request);
        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);

        resultActions.andExpect(status().isUnprocessableEntity());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        AppError response = TestUtilsIT.readJson(content, new TypeReference<>() {});

        assertThat(userRepository.findAll().size()).isEqualTo(1);
        assertThat(response.getMessage()).contains("Пользователь с таким именем уже существует");
        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
    }

    @Test
    @Transactional
    void registrationUserWithIncorrectData() throws Exception {
        AuthRegistrationRequest user = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        user.setConfirmPassword("123412414");

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtilsIT.writeJson(user))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = testUtilsIT.performUnsecuredRequest(request);

        resultActions.andExpect(status().isUnprocessableEntity());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        AppError response = TestUtilsIT.readJson(content, new TypeReference<>() {});

        assertThat(userRepository.findAll().size()).isEqualTo(0);
        assertThat(userRepository.findByUsername(user.getUsername())).isEmpty();;
        assertThat(response.getMessage()).contains("Пароль и потверждающие пароль не совпадают");
        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
    }
}
