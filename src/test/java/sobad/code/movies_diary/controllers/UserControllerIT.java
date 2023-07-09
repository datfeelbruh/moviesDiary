package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.movies_diary.TestConfig;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.MovieRating;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.MovieRatingRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static sobad.code.movies_diary.controller.MovieController.MOVIE_CONTROLLER_USERNAME_MOVIES_PATH;
import static sobad.code.movies_diary.utils.TestUtils.readFixture;
import static sobad.code.movies_diary.utils.TestUtils.readJson;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
public class UserControllerIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieRatingRepository movieRatingRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;

    private static final MovieDtoRequest SAMPLE_MOVIE = readJson(
            readFixture("movies/sampleMovie.json"),
            new TypeReference<MovieDtoRequest>() {
            }
    );

    private static final MovieDtoRequest ANOTHER_MOVIE = readJson(
            readFixture("movies/anotherMovie.json"),
            new TypeReference<MovieDtoRequest>() {
            }
    );

    @BeforeEach
    public void setUp() throws Exception {
        testUtils.clearAll();
        testUtils.setRoles();
        testUtils.regSampleUser();
        testUtils.authSampleUser();
        testUtils.regAnotherUser();
        testUtils.authAnotherUser();
    }

    @Test
    public void getUserMovies() throws Exception {
        testUtils.regSampleMovie();
        Movie movie = movieRepository.findByKpId(SAMPLE_MOVIE.getKpId()).get();
        User user = userRepository.findAll().get(0);
        MovieRating movieRating = movieRatingRepository.findByMovieIdAndUserId(movie.getId(), user.getId()).get();

        MockHttpServletRequestBuilder requestBuilder = get(
                MOVIE_CONTROLLER_USERNAME_MOVIES_PATH, user.getUsername()
        );

        MockHttpServletResponse responseGet = testUtils.perform(requestBuilder).andReturn().getResponse();
        String content = responseGet.getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseGet.getStatus()).isEqualTo(200);
        assertThat(content).contains(movie.getMovieName());
        assertThat(content).contains(movie.getReview());
        assertThat(content).contains(movie.getPosterUrl());
        assertThat(content).contains(String.valueOf(movie.getKpId()));
        assertThat(content).contains(String.valueOf(movie.getImdbRating()));
        assertThat(content).contains(String.valueOf(movie.getReleaseYear()));
        assertThat(content).contains(String.valueOf(movie.getKpRating()));
        assertThat(user.getUserMovieRatings().size()).isEqualTo(1);
        assertThat(user.getUserMovieRatings()).contains(movieRating);
        assertThat(user.getMovies().size()).isEqualTo(1);
        assertThat(user.getMovies()).contains(movie);

        testUtils.regAnotherMovie();

        Movie anotherMovie = movieRepository.findByKpId(ANOTHER_MOVIE.getKpId()).get();
        User anotherUser = userRepository.findAll().get(1);
        MovieRating anotherMovieRating =
                movieRatingRepository.findByMovieIdAndUserId(anotherMovie.getId(), anotherUser.getId()).get();

        MockHttpServletRequestBuilder anotherRequestBuilder = get(
                MOVIE_CONTROLLER_USERNAME_MOVIES_PATH, anotherUser.getUsername()
        );

        MockHttpServletResponse anotherResponseGet = testUtils.perform(anotherRequestBuilder).andReturn().getResponse();
        String anotherContent = anotherResponseGet.getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseGet.getStatus()).isEqualTo(200);
        assertThat(anotherContent).contains(anotherMovie.getMovieName());

        assertThat(anotherUser.getUserMovieRatings().size()).isEqualTo(1);
        assertThat(anotherUser.getUserMovieRatings()).contains(anotherMovieRating);
        assertThat(anotherUser.getMovies().size()).isEqualTo(1);
        assertThat(anotherUser.getMovies()).contains(anotherMovie);
    }
}
