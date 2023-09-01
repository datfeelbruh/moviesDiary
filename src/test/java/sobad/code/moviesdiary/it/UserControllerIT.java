package sobad.code.moviesdiary.it;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.moviesdiary.ConfigForTests;
import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.user.UserDtoAboutRequest;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.repositories.UserRepository;
import sobad.code.moviesdiary.utils.TestUtils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.moviesdiary.ConfigForTests.TEST_PROFILE;
import static sobad.code.moviesdiary.controllers.UserController.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigForTests.class)
@ActiveProfiles(TEST_PROFILE)
class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils testUtilsIT;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registrationUserWithCorrectData() throws Exception {
        UserRegistrationDtoRequest user = TestUtils.readJson(
                TestUtils.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(user))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isCreated());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        UserDtoResponse response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findByUsername(user.getUsername())).isNotNull();
        assertThat(userRepository.findByUsername(user.getUsername()).get().getPassword())
                .isNotEqualTo(user.getPassword());
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void registrationUserWithExistedUsername() throws Exception {
        testUtilsIT.createSampleUser();
        UserRegistrationDtoRequest user = TestUtils.readJson(
                TestUtils.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );


        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(user))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isUnprocessableEntity());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(response.getMessage()).contains("Пользователь с таким именем или email уже существует");
        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void registrationUserWithIncorrectData() throws Exception {
        UserRegistrationDtoRequest user = TestUtils.readJson(
                TestUtils.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        user.setConfirmPassword("123412414");

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(user))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isUnprocessableEntity());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(userRepository.findByUsername(user.getUsername())).isEmpty();
        assertThat(response.getMessage()).contains("Пароль и потверждающий пароль не совпадают");
        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
    }

    @Test
    @WithMockUser("sobad")
    void uploadUserImage() throws Exception {
        testUtilsIT.createSampleUser();
        User user = userRepository.findAll().get(0);

        assertThat(user.getAvatar()).isNull();

        MockPart part = new MockPart("image", "file.png", new byte[] {1});
        part.getHeaders().setContentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE));
        MockHttpServletRequestBuilder request = multipart(USER_CONTROLLER_PATH + "/avatar")
                .part(part)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        ResultActions result = testUtilsIT.performWithToken(request, user).andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        UserDtoResponse response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getAvatar()).isNotNull();
    }

    @Test
    @WithMockUser("sobad")
    void uploadUserImageWithIncorrectContentType() throws Exception {
        testUtilsIT.createSampleUser();
        User user = userRepository.findAll().get(0);

        MockPart part = new MockPart("image", "file.html", new byte[] {1});
        part.getHeaders().setContentType(MediaType.valueOf(MediaType.TEXT_HTML_VALUE));
        MockHttpServletRequestBuilder request = multipart(USER_CONTROLLER_PATH + "/avatar")
                .part(part)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        ResultActions result = testUtilsIT.performWithToken(request, user).andExpect(status().isUnprocessableEntity());
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response).isNotNull();
    }

    @Test
    @WithMockUser("sobad")
    void updateAbout() throws Exception {
        testUtilsIT.createSampleUser();
        User user = userRepository.findAll().get(0);

        assertThat(userRepository.findAll().get(0).getAbout()).isNull();

        MockHttpServletRequestBuilder request = put(USER_CONTROLLER_PATH + "/" + user.getId())
                .content(TestUtils.writeJson(new UserDtoAboutRequest("new About")))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = testUtilsIT.performWithToken(request, user);

        resultActions.andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        UserDtoResponse response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getAbout()).isNotNull();
        assertThat(userRepository.findAll().get(0).getAbout()).isNotNull();
    }

    @Test
    @WithMockUser("sobad")
    void updateAboutFromAnotherUser() throws Exception {
        testUtilsIT.createSampleUser();
        testUtilsIT.createAnotherUser();
        User user = userRepository.findAll().get(1);
        assertThat(userRepository.findAll().get(0).getAbout()).isNull();

        MockHttpServletRequestBuilder request = put(USER_CONTROLLER_PATH + "/" + user.getId())
                .content(TestUtils.writeJson(new UserDtoAboutRequest("new About")))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = testUtilsIT.performWithToken(request, user);

        resultActions.andExpect(status().isForbidden());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ResponseMessage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response).isNotNull();
        assertThat(userRepository.findAll().get(0).getAbout()).isNull();
    }
}

