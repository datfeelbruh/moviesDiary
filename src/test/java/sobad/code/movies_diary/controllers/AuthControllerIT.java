package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrationUser() throws Exception {
        String data = null;
        try {
            data = Files.readString(Path.of("src/test/resources/fixtures/users/sampleUser.json")
                    .toAbsolutePath().normalize());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        AuthRegistrationRequest registrationRequest = objectMapper.readValue(
                data,
                new TypeReference<AuthRegistrationRequest>() {}
        );

        String content = null;

        try {
            content = objectMapper.writeValueAsString(registrationRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration")
                .content(content)
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestBuilder);


    }
}
