package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRatingRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtilsIT;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.ExternalApiController.EXTERNAL_API_CONTROLLER_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Transactional
public class MovieControllerIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRatingRepository movieRatingRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TestUtilsIT testUtilsIT;

    @Test
    @Transactional
    void createMovie() throws Exception {
        testUtilsIT.createSampleUser();
        testUtilsIT.authSampleUser();

        User user = userRepository.findAll().get(0);
        Token token = tokenRepository.findAll().get(0);

        MovieDtoRequest requestDto = TestUtilsIT.readJson(
                TestUtilsIT.readFixture("movies/sampleMovie.json"),
                new TypeReference<>() {}
        );


        MockHttpServletRequestBuilder request = post(MOVIE_CONTROLLER_PATH)
                .content(TestUtilsIT.writeJson(requestDto))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = testUtilsIT.performSecuredRequest(request, user);

        resultActions.andExpect(status().isCreated());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        MovieDtoResponse dtoResponse = TestUtilsIT.readJson(responseBody, new TypeReference<>(){});
        Movie movie = movieRepository.findAll().get(0);

        assertThat(movie.getMovieName()).contains(dtoResponse.getMovieName());
        assertThat(movie.getKpId()).isEqualTo(dtoResponse.getKpId());
        assertThat(movie.getPosterUrl()).contains(dtoResponse.getPosterUrl());
        assertThat(movie.getReview()).isNotEmpty();
        assertThat(movie.getGenres()).containsAll(dtoResponse.getGenres());
    }
}
