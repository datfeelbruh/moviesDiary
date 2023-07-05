package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRatingRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.RoleRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.JwtTokenUtils;

import java.util.Map;

@Component
public class TestUtil {
    public static final String AUTH_FIXTURES_PATH = "src/test/resources/fixtures/auth";
    public static final String USERS_FIXTURES_PATH = "src/test/resources/fixtures/users";
    public static final String MOVIES_FIXTURES_PATH = "src/test/resources/fixtures/movies";

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

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        movieRepository.deleteAll();
        movieRatingRepository.deleteAll();
        genreRepository.deleteAll();
    }
}
