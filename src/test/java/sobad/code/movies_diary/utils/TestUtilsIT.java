package sobad.code.movies_diary.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.authentication.AuthLoginRequest;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.jwts.JwtTokenUtils;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRatingRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.RoleRepository;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static sobad.code.movies_diary.controllers.UserController.USER_CONTROLLER_PATH;

@Component
public class TestUtilsIT {

    public static final String FIXTURES_PATH = "src/test/resources/fixtures/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRatingRepository movieRatingRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void clear() {
        movieRepository.deleteAll();
        movieRatingRepository.deleteAll();
        genreRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

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

    public ResultActions performSecuredRequest(final MockHttpServletRequestBuilder request, User user)
            throws Exception {
        final String token = jwtTokenUtils.generateAccessToken(userService.loadUserByUsername(user.getUsername()));
        request.header(AUTHORIZATION, "Bearer " + token);
        return mockMvc.perform(request);
    }

    public ResultActions performUnsecuredRequest(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public void createSampleUser() throws Exception {
        AuthRegistrationRequest user = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("users/sampleUser.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtilsIT.writeJson(user))
                .contentType(APPLICATION_JSON);

        performUnsecuredRequest(request);
    }

    public void authSampleUser() throws Exception {
        AuthLoginRequest userAuth = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("auth/sampleAuth.json"),
                new TypeReference<>() {
                }
        );

        MockHttpServletRequestBuilder request = post(AUTH_CONTROLLER_LOGIN_PATH)
                .content(TestUtilsIT.writeJson(userAuth))
                .contentType(APPLICATION_JSON);

        performUnsecuredRequest(request);
    }
}
