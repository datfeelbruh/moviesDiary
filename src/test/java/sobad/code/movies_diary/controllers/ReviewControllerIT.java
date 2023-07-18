package sobad.code.movies_diary.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtils;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.stream;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH;
import static sobad.code.movies_diary.controllers.ReviewController.REVIEW_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class ReviewControllerIT {
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
    void createReview() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                        ReviewDtoRequest.builder()
                                .movieId(movie.getId())
                                .review("good")
                                .rating(10.0)
                                .build()
                        )
                )
                .contentType(APPLICATION_JSON);



        ResultActions result = mockMvc.perform(createReviewRequest);
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewDtoResponse response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getMovieId()).isEqualTo(movie.getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void updateReview() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON);

        mockMvc.perform(createReviewRequest);

        MockHttpServletRequestBuilder updateReviewRequest = put(REVIEW_CONTROLLER_PATH + "/1")
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("bad")
                                        .rating(0.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON);

        ResultActions result = mockMvc.perform(updateReviewRequest);
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewDtoResponse response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.getMovieId()).isEqualTo(movie.getId());
        assertThat(response.getUserReview().getReview()).isEqualTo("bad");
        assertThat(response.getUserReview().getRating()).isEqualTo(0.0);
    }

    @Test
    @Transactional
    void getReviewByUserId() throws Exception {
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

        MockHttpServletRequestBuilder getReviewByUser = get(REVIEW_CONTROLLER_PATH)
                .param("userId", user.getId().toString())
                .with(user("sobad").password("sobad"));

        ResultActions resultActions = mockMvc.perform(getReviewByUser);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<ReviewDtoResponse> response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.get(0).getUsername()).isEqualTo(user.getUsername());
        assertThat(response.get(0).getUsername()).isNotEqualTo(user2.getUsername());
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getMovieId()).isEqualTo(movie.getId());

        MockHttpServletRequestBuilder getReviewByUser2 = get(REVIEW_CONTROLLER_PATH)
                .param("userId", user2.getId().toString())
                .with(user("sobad").password("sobad"));


        ResultActions resultActions2 = mockMvc.perform(getReviewByUser2);
        String content2 = resultActions2.andReturn().getResponse().getContentAsString(UTF_8);

        List<ReviewDtoResponse> response2 = TestUtils.readJson(content2, new TypeReference<>() {});

        assertThat(response2.get(0).getUsername()).isEqualTo(user2.getUsername());
        assertThat(response2.get(0).getUsername()).isNotEqualTo(user.getUsername());
        assertThat(response.size()).isEqualTo(1);
        assertThat(response2.get(0).getMovieId()).isEqualTo(movie2.getId());
    }

    @Test
    @Transactional
    void getReviewByMovieID() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true")
                .with(user("sobad").password("sobad"));

        mockMvc.perform(request);

        Movie movie = movieRepository.findAll().get(0);
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
                                        .movieId(movie.getId())
                                        .review("bad")
                                        .rating(0.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));


        mockMvc.perform(createReviewRequest2);

        MockHttpServletRequestBuilder getReviewByUser = get(REVIEW_CONTROLLER_PATH)
                .param("movieId", movie.getId().toString())
                .with(user("sobad").password("sobad"));

        ResultActions resultActions = mockMvc.perform(getReviewByUser);
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<ReviewDtoResponse> response = TestUtils.readJson(content, new TypeReference<>() {});

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getUsername()).isEqualTo(user.getUsername());
        assertThat(response.get(1).getUsername()).isEqualTo(user2.getUsername());
        assertThat(response.get(0).getUserReview().getRating()).isEqualTo(10.0);
        assertThat(response.get(1).getUserReview().getRating()).isEqualTo(0.0);
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void deleteReview() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder request = get(MOVIE_CONTROLLER_PATH)
                .param("movieName", "Человек-паук")
                .param("findKp", "true")
                .param("expanded", "true");

        mockMvc.perform(request).andExpect(status().isOk());

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON);

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());
        assertThat(reviewRepository.findAll().size()).isEqualTo(1);
        Review review = reviewRepository.findAll().get(0);

        MockHttpServletRequestBuilder deleteReview = delete(REVIEW_CONTROLLER_PATH + "/" + review.getId());
        mockMvc.perform(deleteReview).andExpect(status().isOk());

        assertThat(reviewRepository.findAll().size()).isEqualTo(0);
    }
}
