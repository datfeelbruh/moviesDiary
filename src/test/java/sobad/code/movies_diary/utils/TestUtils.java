package sobad.code.movies_diary.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.dto.Dto;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.entities.Role;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.jwts.JwtTokenUtils;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_REG_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH;

@Component
public class TestUtils {
    public static final String FIXTURES_PATH = "src/test/resources/fixtures/";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRatingRepository movieRatingRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static final AuthRegistrationRequest SAMPLE_USER_REG = readJson(
            readFixture("users/sampleUser.json"),
            new TypeReference<AuthRegistrationRequest>() {
            }
    );

    public static final AuthRegistrationRequest SAMPLE_USER_AUTH = readJson(
            readFixture("auth/sampleAuth.json"),
            new TypeReference<AuthRegistrationRequest>() {
            }
    );

    public static final AuthRegistrationRequest ANOTHER_USER_REG = readJson(
            readFixture("users/anotherUser.json"),
            new TypeReference<AuthRegistrationRequest>() {
            }
    );

    public static final AuthRegistrationRequest ANOTHER_USER_AUTH = readJson(
            readFixture("auth/anotherAuth.json"),
            new TypeReference<AuthRegistrationRequest>() {
            }
    );

    public static final MovieDtoRequest SAMPLE_MOVIE = readJson(
            readFixture("movies/sampleMovie.json"),
            new TypeReference<MovieDtoRequest>() {
            }
    );

    public static final MovieDtoRequest ANOTHER_MOVIE = readJson(
            readFixture("movies/anotherMovie.json"),
            new TypeReference<MovieDtoRequest>() {
            }
    );

    public void setRoles() {
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));
    }
    public void clearAll() {
        tokenRepository.deleteAll();
        movieRatingRepository.deleteAll();
        userRepository.deleteAll();
        movieRepository.deleteAll();
        genreRepository.deleteAll();
        roleRepository.deleteAll();
    }

    public static String readFixture(String path) {
        try {
            return Files.readString(Path.of(FIXTURES_PATH + path).toAbsolutePath().normalize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeJson(final Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readJson(final String json, final TypeReference<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions regEntity(Dto dto, String fixturePath) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(fixturePath)
                .content(writeJson(dto))
                .contentType(APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, User user) throws Exception {
        final String token = jwtTokenUtils.generateAccessToken(userService.loadUserByUsername(user.getUsername()));
        request.header(AUTHORIZATION, "Bearer " + token);
        return mockMvc.perform(request);
    }

    public void regSampleUser() throws Exception {
        regEntity(SAMPLE_USER_REG, AUTH_CONTROLLER_REG_PATH);
    }

    public void regAnotherUser() throws Exception {
        regEntity(ANOTHER_USER_REG, AUTH_CONTROLLER_REG_PATH);
    }

    public void authSampleUser() throws Exception {
        regEntity(SAMPLE_USER_AUTH, AUTH_CONTROLLER_LOGIN_PATH);
    }

    public void authAnotherUser() throws Exception {
        regEntity(ANOTHER_USER_AUTH, AUTH_CONTROLLER_LOGIN_PATH);
    }

    public ResultActions regSampleMovie() throws Exception {
        User user = userRepository.findAll().get(0);
        MockHttpServletRequestBuilder request = post(MOVIE_CONTROLLER_PATH)
                .content(TestUtils.writeJson(SAMPLE_MOVIE))
                .contentType(APPLICATION_JSON);

        return perform(request, user);
    }

    public ResultActions regAnotherMovie() throws Exception {
        User user = userRepository.findAll().get(1);
        MockHttpServletRequestBuilder request = post(MOVIE_CONTROLLER_PATH)
                .content(TestUtils.writeJson(ANOTHER_MOVIE))
                .contentType(APPLICATION_JSON);

        return perform(request, user);
    }
}
