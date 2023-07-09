package sobad.code.movies_diary.controllers;

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
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_ALL_MOVIES_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_CREATE_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_GENRES_PATH;
import static sobad.code.movies_diary.utils.TestUtils.ANOTHER_MOVIE;
import static sobad.code.movies_diary.utils.TestUtils.SAMPLE_MOVIE;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
public class MovieControllerIT {
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

    @BeforeEach
    public void setUp() throws Exception {
        testUtils.clearAll();
        testUtils.setRoles();
        testUtils.regSampleUser();
        testUtils.authSampleUser();
        testUtils.regAnotherUser();
        testUtils.authAnotherUser();
        testUtils.regSampleMovie();
    }

    @Test
    public void createMovie() throws Exception {
        User user = userRepository.findAll().get(0);
        MockHttpServletRequestBuilder request = post(MOVIE_CONTROLLER_CREATE_PATH)
                .content(TestUtils.writeJson(ANOTHER_MOVIE))
                .contentType(APPLICATION_JSON);

        MockHttpServletResponse response = testUtils.perform(request, user).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(201);

        Movie movie = movieRepository.findByKpId(ANOTHER_MOVIE.getKpId()).get();
        MovieRating movieRating = movieRatingRepository.findByMovieIdAndUserId(movie.getId(), user.getId()).get();
        String content = response.getContentAsString(StandardCharsets.UTF_8);

        assertThat(content).contains(movie.getMovieName());
        assertThat(content).contains(movie.getReview());
        assertThat(content).contains(movie.getPosterUrl());
        assertThat(content).contains(String.valueOf(movie.getKpId()));
        assertThat(content).contains(String.valueOf(movie.getImdbRating()));
        assertThat(content).contains(String.valueOf(movie.getReleaseYear()));
        assertThat(content).contains(String.valueOf(movie.getKpRating()));

        user = userRepository.findAll().get(0);

        assertThat(user.getUserMovieRatings().size()).isGreaterThan(0);
        assertThat(user.getUserMovieRatings()).contains(movieRating);

        assertThat(user.getMovies().size()).isGreaterThan(0);
        assertThat(user.getMovies()).contains(movie);
    }

    @Test
    public void getAllMovies() throws Exception {
        testUtils.regAnotherMovie();
        Integer size = movieRepository.findAll().size();
        Movie movie = movieRepository.findByKpId(SAMPLE_MOVIE.getKpId()).get();
        User user = userRepository.findAll().get(0);
        MovieRating movieRating = movieRatingRepository.findByMovieIdAndUserId(movie.getId(), user.getId()).get();
        Movie anotherMovie = movieRepository.findByKpId(ANOTHER_MOVIE.getKpId()).get();
        User anotherUser = userRepository.findAll().get(1);
        MovieRating anotherMovieRating =
                movieRatingRepository.findByMovieIdAndUserId(anotherMovie.getId(), anotherUser.getId()).get();

        MockHttpServletRequestBuilder requestBuilder = get(
                MOVIE_CONTROLLER_ALL_MOVIES_PATH
        );

        MockHttpServletResponse responseGet = testUtils.perform(requestBuilder).andReturn().getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);

        String content = responseGet.getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseGet.getStatus()).isEqualTo(200);

        assertThat(content).contains(movie.getMovieName());
        assertThat(content).contains(movie.getReview());
        assertThat(content).contains(movie.getPosterUrl());
        assertThat(content).contains(String.valueOf(movie.getKpId()));
        assertThat(content).contains(String.valueOf(movie.getImdbRating()));
        assertThat(content).contains(String.valueOf(movie.getReleaseYear()));
        assertThat(content).contains(String.valueOf(movie.getKpRating()));
        assertThat(user.getUserMovieRatings()).contains(movieRating);

        assertThat(content).contains(anotherMovie.getMovieName());
        assertThat(content).contains(anotherMovie.getReview());
        assertThat(content).contains(anotherMovie.getPosterUrl());
        assertThat(content).contains(String.valueOf(anotherMovie.getKpId()));
        assertThat(content).contains(String.valueOf(anotherMovie.getImdbRating()));
        assertThat(content).contains(String.valueOf(anotherMovie.getReleaseYear()));
        assertThat(content).contains(String.valueOf(anotherMovie.getKpRating()));
        assertThat(anotherUser.getUserMovieRatings()).contains(anotherMovieRating);
    }

    @Test
    public void getMoviesByGenre() throws Exception {
        testUtils.regSampleMovie();
        testUtils.regAnotherMovie();
        Movie movie = movieRepository.findAll().get(0);
        Movie anotherMovie = movieRepository.findAll().get(1);

        MockHttpServletRequestBuilder requestBuilder = get(
                MOVIE_CONTROLLER_GENRES_PATH + "?genre=комедия"
        );

        MockHttpServletResponse genreResponse = testUtils.perform(requestBuilder).andReturn().getResponse();
        String genreResponseContent = genreResponse.getContentAsString(StandardCharsets.UTF_8);

        assertThat(genreResponse.getStatus()).isEqualTo(200);
        assertThat(genreResponseContent).contains(movie.getMovieName());
        assertThat(genreResponseContent).doesNotContain(anotherMovie.getMovieName());

        MockHttpServletRequestBuilder anotherRequestBuilder = get(
                MOVIE_CONTROLLER_GENRES_PATH + "?genre=боевик"
        );

        MockHttpServletResponse anotherGenreResponse = testUtils.perform(anotherRequestBuilder).andReturn().getResponse();
        String anotherGenreResponseContent = anotherGenreResponse.getContentAsString(StandardCharsets.UTF_8);

        assertThat(anotherGenreResponse.getStatus()).isEqualTo(200);
        assertThat(anotherGenreResponseContent).contains(movie.getMovieName());
        assertThat(anotherGenreResponseContent).contains(anotherMovie.getMovieName());
    }
}
