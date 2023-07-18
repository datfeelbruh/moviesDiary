package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.dto.movie.MovieDtoShortInfo;
import sobad.code.movies_diary.dto.movie.UserMoviesDtoResponse;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.entities.Genre;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.service.UserService;
import sobad.code.movies_diary.utils.MovieTestDto;
import sobad.code.movies_diary.utils.TestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH_USERS;
import static sobad.code.movies_diary.controllers.ReviewController.REVIEW_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
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
    private TokenRepository tokenRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private TestUtils testUtils;

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieById() throws Exception {
        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder getMovieByIdRequest = get(MOVIE_CONTROLLER_PATH + "/" + movie.getId());

        ResultActions result = mockMvc.perform(getMovieByIdRequest);
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MovieDtoResponse response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getId()).isEqualTo(movie.getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByNonExistedId() throws Exception {
        MockHttpServletRequestBuilder getMovieByIdRequest = get(MOVIE_CONTROLLER_PATH + "/123");
        ResultActions resultActions = mockMvc.perform(getMovieByIdRequest);

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        AppError appError = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(appError.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
        assertThat(appError.getMessage()).isEqualTo("Фильм с данным id '123' не найден");
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMoviesByUsername() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder requestMoviesByUsername = get(
                MOVIE_CONTROLLER_PATH_USERS + "/" + user.getUsername());

        ResultActions result = mockMvc.perform(requestMoviesByUsername).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);

        UserMoviesDtoResponse response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies().size()).isEqualTo(0);

        ReviewDtoRequest reviewDtoRequest = new ReviewDtoRequest(movie.getId(), "good", 10.0);

        MockHttpServletRequestBuilder requestCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(reviewDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateReview).andExpect(status().isCreated());

        result = mockMvc.perform(requestMoviesByUsername).andExpect(status().isOk());

        content = result.andReturn().getResponse().getContentAsString(UTF_8);

        response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies().size()).isEqualTo(1);
        assertThat(response.getMovies().get(0).getId()).isEqualTo(movie.getId());
    }

    @Test
    @Transactional
    void getMoviesByUsernameWithDifferentUsers() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true")
                .with(user("sobad").password("sobad"));

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);
        Movie movie2 = movieRepository.findAll().get(1);
        User user = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));

        mockMvc.perform(createReviewRequest);

        MockHttpServletRequestBuilder createReviewRequest2 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie2.getId())
                                        .review("bad")
                                        .rating(0.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));


        mockMvc.perform(createReviewRequest2);

        MockHttpServletRequestBuilder getMovieByUsername = get(
                MOVIE_CONTROLLER_PATH_USERS + "/" + user.getUsername()
        );


        ResultActions resultActions = mockMvc.perform(getMovieByUsername);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        UserMoviesDtoResponse response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies().size()).isEqualTo(1);
        assertThat(response.getMovies().get(0).getId()).isEqualTo(movie.getId());

        MockHttpServletRequestBuilder getMovieByUsername2 = get(
                MOVIE_CONTROLLER_PATH_USERS + "/" + user2.getUsername()
        );

        ResultActions resultActions2 = mockMvc.perform(getMovieByUsername2);
        String content2 = resultActions2.andReturn().getResponse().getContentAsString(UTF_8);
        UserMoviesDtoResponse response2 = TestUtils.readJson(content2, new TypeReference<>() {});

        assertThat(response2.getUsername()).isEqualTo(user2.getUsername());
        assertThat(response2.getMovies().size()).isEqualTo(1);
        assertThat(response2.getMovies().get(0).getId()).isEqualTo(movie2.getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMoviesByNonExistedUsername() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByUsername = get(
                MOVIE_CONTROLLER_PATH_USERS + "/qwer");

        ResultActions resultActions = mockMvc.perform(requestMoviesByUsername);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        AppError appError = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(appError.getStatusCode()).isEqualTo(UNAUTHORIZED.value());
        assertThat(appError.getMessage()).isEqualTo("Пользователь с таким именем 'qwer' не найден");
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByNameAndFindKpAndExpanded() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        ResultActions resultActions = mockMvc.perform(requestMoviesByName);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoResponse> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
        assertThat(movieDtoResponse.get(0).getTitle()).contains("Человек-паук");
        assertThat(movieDtoResponse.get(0).getDescription()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByNameAndFindKp() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true");

        ResultActions resultActions = mockMvc.perform(requestMoviesByName);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoShortInfo> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
        assertThat(movieDtoResponse.get(0).getTitle()).contains("Человек-паук");
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByNameAndExpanded() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(requestMoviesByName);

        MockHttpServletRequestBuilder requestMoviesByNameFromDb = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("expanded", "true");

        ResultActions resultActions = mockMvc.perform(requestMoviesByNameFromDb);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoResponse> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
        assertThat(movieDtoResponse.get(0).getTitle()).contains("Человек-паук");
        assertThat(movieDtoResponse.get(0).getDescription()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByName() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(requestMoviesByName);

        MockHttpServletRequestBuilder requestMoviesByNameFromDb = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук");

        ResultActions resultActions = mockMvc.perform(requestMoviesByNameFromDb);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoShortInfo> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
        assertThat(movieDtoResponse.get(0).getTitle()).contains("Человек-паук");
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByGenreAndExpanded() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(requestMoviesByName);

        MockHttpServletRequestBuilder requestMoviesByGenreFromDb = get(MOVIE_CONTROLLER_PATH)
                .param("genreName", "боевик")
                .param("expanded", "true");

        ResultActions resultActions = mockMvc.perform(requestMoviesByGenreFromDb);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoResponse> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
        assertThat(movieDtoResponse.get(0).getGenres()).contains(new GenreDto("боевик"));
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieByGenre() throws Exception {
        MockHttpServletRequestBuilder requestMoviesByName = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(requestMoviesByName);

        MockHttpServletRequestBuilder requestMoviesByGenreFromDb = get(MOVIE_CONTROLLER_PATH)
                .param("genreName", "боевик");

        ResultActions resultActions = mockMvc.perform(requestMoviesByGenreFromDb);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieDtoShortInfo> movieDtoResponse = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(movieDtoResponse.size()).isGreaterThan(0);
    }
}
