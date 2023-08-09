package sobad.code.moviesdiary.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.moviesdiary.dtos.user.UserRegistrationDtoRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static sobad.code.moviesdiary.controllers.UserController.USER_CONTROLLER_PATH;

@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;
    public static final String FIXTURES_PATH = "src/test/resources/fixtures/";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String readFixture(String path) {
        try {
            return Files.readString(Path.of(FIXTURES_PATH + path).toAbsolutePath().normalize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeJson(final Object json) {
        try {
            return MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readJson(final String jsonString, final TypeReference<T> type) {
        try {
            return MAPPER.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void createSampleUser() throws Exception {
        UserRegistrationDtoRequest user = TestUtils.readJson(
                TestUtils.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(user))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request);
    }

    public void createAnotherUser() throws Exception {
        UserRegistrationDtoRequest user = TestUtils.readJson(
                TestUtils.readFixture("users/anotherUser.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(user))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request);
    }

    public void createUser(UserRegistrationDtoRequest authRequest) throws Exception {
        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(authRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request);
    }
}