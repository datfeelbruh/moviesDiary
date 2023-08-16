package sobad.code.moviesdiary.it;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobad.code.moviesdiary.dtos.review.ReviewDto;
import sobad.code.moviesdiary.dtos.review.ReviewDtoRequest;
import sobad.code.moviesdiary.dtos.pages.ReviewPages;
import sobad.code.moviesdiary.entities.Genre;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.Review;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.repositories.GenreRepository;
import sobad.code.moviesdiary.repositories.MovieRepository;
import sobad.code.moviesdiary.repositories.ReviewRepository;
import sobad.code.moviesdiary.repositories.UserRepository;
import sobad.code.moviesdiary.utils.TestUtils;

import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.moviesdiary.controllers.ReviewController.REVIEW_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Slf4j
class ReviewControllerIT {
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
    private ReviewRepository reviewRepository;
    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void beforeEach() {
        Set<Genre> genres1 = Set.of(
                new Genre("приключения"),
                new Genre("комедия"),
                new Genre("боевик"),
                new Genre("фантастика")
        );
        Set<Genre> genres2 = Set.of(
                new Genre("фэнтези"),
                new Genre("мультфильм")
        );

        genreRepository.saveAll(genres1);
        genreRepository.saveAll(genres2);

        movieRepository.save(
                Movie.builder()
                        .title("Человек-паук: Вдали от дома")
                        .id(1008445L)
                        .description("description")
                        .posterUrl("https://st.kp.yandex.net/images/film_big/1008445.jpg")
                        .releaseYear(2019)
                        .kpRating(7.424)
                        .imdbRating(7.4)
                        .genres(genres1)
                        .build()
        );

        movieRepository.save(
                Movie.builder()
                        .title("Человек-паук")
                        .id(838L)
                        .description("description")
                        .posterUrl("https://st.kp.yandex.net/images/film_big/838.jpg")
                        .releaseYear(2002)
                        .kpRating(7.698)
                        .imdbRating(7.4)
                        .genres(genres1)
                        .build()
        );

        movieRepository.save(
                Movie.builder()
                        .title("Человек-паук: Через вселенные")
                        .id(920265L)
                        .description("description")
                        .posterUrl("https://st.kp.yandex.net/images/film_big/920265.jpg")
                        .releaseYear(2018)
                        .kpRating(8.153)
                        .imdbRating(8.4)
                        .genres(genres2)
                        .build()
        );

        movieRepository.save(
                Movie.builder()
                        .title("Человек-паук: Возвращение домой")
                        .id(690593L)
                        .description("description")
                        .posterUrl("https://st.kp.yandex.net/images/film_big/690593.jpg")
                        .releaseYear(2017)
                        .kpRating(7.156)
                        .imdbRating(7.4)
                        .genres(genres1)
                        .build()
        );

        movieRepository.save(
                Movie.builder()
                        .title("Человек-паук 3: Враг в отражении")
                        .id(82441L)
                        .description("description")
                        .posterUrl("https://st.kp.yandex.net/images/film_big/82441.jpg")
                        .releaseYear(2007)
                        .kpRating(7.105)
                        .imdbRating(6.3)
                        .genres(genres2)
                        .build()
        );
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void createReview() throws Exception {
        testUtils.createSampleUser();

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
        ReviewDto response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(reviewRepository.findAll().get(0).getMovie().getId()).isEqualTo(response.getMovieId());
        assertThat(reviewRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    void getReviewByUser() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        User sobad = userRepository.findAll().get(0);
        User datfeel = userRepository.findAll().get(1);
        Movie sobadMovie = movieRepository.findAll().get(0);
        Movie datfeelMovie = movieRepository.findAll().get(1);

        MockHttpServletRequestBuilder createSobadReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(sobadMovie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));


        MockHttpServletRequestBuilder createDatfeelRReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(datfeelMovie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(createSobadReview);
        mockMvc.perform(createDatfeelRReview);

        MockHttpServletRequestBuilder getReviewBySobad = get(REVIEW_CONTROLLER_PATH)
                .param("userId", String.valueOf(sobad.getId()))
                .with(user("sobad").password("sobad"));

        MockHttpServletRequestBuilder getReviewByDatfeel = get(REVIEW_CONTROLLER_PATH)
                .param("userId", String.valueOf(datfeel.getId()))
                .with(user("datfeel").password("datfeel"));

        ResultActions sobadResult = mockMvc.perform(getReviewBySobad).andExpect(status().isOk());

        String sobadContent = sobadResult.andReturn().getResponse().getContentAsString(UTF_8);

        ResultActions datfeelResult = mockMvc.perform(getReviewByDatfeel).andExpect(status().isOk());

        String datfeelContent = datfeelResult.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewPages sobadResponse = TestUtils.readJson(sobadContent, new TypeReference<>() { });
        ReviewPages datfeelResponse = TestUtils.readJson(datfeelContent, new TypeReference<>() { });

        assertThat(sobadResponse.getReviews().get(0).getUser().getId()).isEqualTo(sobad.getId());
        assertThat(sobadResponse.getReviews().get(0).getMovieId()).isEqualTo(sobadMovie.getId());
        assertThat(sobadResponse.getReviews()).hasSize(1);
        assertThat(datfeelResponse.getReviews().get(0).getUser().getId()).isEqualTo(datfeel.getId());
        assertThat(datfeelResponse.getReviews().get(0).getMovieId()).isEqualTo(datfeelMovie.getId());
        assertThat(datfeelResponse.getReviews()).hasSize(1);
    }

    @Test
    @Transactional
    void getReviewByMovie() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createSobadReview = post(REVIEW_CONTROLLER_PATH)
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

        mockMvc.perform(createSobadReview);

        MockHttpServletRequestBuilder createDatfeelReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(createDatfeelReview);

        MockHttpServletRequestBuilder getReviewByMovie = get(REVIEW_CONTROLLER_PATH)
                .param("movieId", String.valueOf(movie.getId()))
                .with(user("sobad").password("sobad"));

        ResultActions getResult = mockMvc.perform(getReviewByMovie).andExpect(status().isOk());
        String content = getResult.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewPages response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getReviews().get(0).getUser().getUsername()).isEqualTo("sobad");
        assertThat(response.getReviews().get(1).getUser().getUsername()).isEqualTo("datfeel");
        assertThat(response.getReviews().get(0).getMovieId()).isEqualTo(movie.getId());
        assertThat(response.getReviews().get(1).getMovieId()).isEqualTo(movie.getId());
    }

    @Test
    @Transactional
    void getReviewByUserAndMovie() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        User sobad = userRepository.findAll().get(0);
        User datfeel = userRepository.findAll().get(1);
        Movie sobadMovie = movieRepository.findAll().get(0);
        Movie datfeelMovie = movieRepository.findAll().get(1);

        MockHttpServletRequestBuilder createSobadReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(sobadMovie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));


        MockHttpServletRequestBuilder createDatfeelRReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(datfeelMovie.getId())
                                        .review("bad")
                                        .rating(0.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(createSobadReview);
        mockMvc.perform(createDatfeelRReview);

        MockHttpServletRequestBuilder getReviewBySobad = get(REVIEW_CONTROLLER_PATH)
                .param("userId", String.valueOf(sobad.getId()))
                .param("movieId", String.valueOf(sobadMovie.getId()))
                .with(user("sobad").password("sobad"));

        MockHttpServletRequestBuilder getReviewByDatfeel = get(REVIEW_CONTROLLER_PATH)
                .param("userId", String.valueOf(datfeel.getId()))
                .param("movieId", String.valueOf(datfeelMovie.getId()))
                .with(user("datfeel").password("datfeel"));

        ResultActions sobadResult = mockMvc.perform(getReviewBySobad).andExpect(status().isOk());
        String sobadContent = sobadResult.andReturn().getResponse().getContentAsString(UTF_8);
        ResultActions datfeelResult = mockMvc.perform(getReviewByDatfeel).andExpect(status().isOk());
        String datfeelContent = datfeelResult.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewDto sobadResponse = TestUtils.readJson(sobadContent, new TypeReference<>() { });
        ReviewDto datfeelResponse = TestUtils.readJson(datfeelContent, new TypeReference<>() { });

        assertThat(sobadResponse.getUser().getId()).isEqualTo(sobad.getId());
        assertThat(sobadResponse.getMovieId()).isEqualTo(sobadMovie.getId());
        assertThat(sobadResponse.getRating()).isEqualTo(10.0);
        assertThat(sobadResponse.getReview()).isEqualTo("good");
        assertThat(datfeelResponse.getUser().getId()).isEqualTo(datfeel.getId());
        assertThat(datfeelResponse.getMovieId()).isEqualTo(datfeelMovie.getId());
        assertThat(datfeelResponse.getRating()).isEqualTo(0.0);
        assertThat(datfeelResponse.getReview()).isEqualTo("bad");
    }

    @Test
    @Transactional
    void getAllReviews() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        User sobad = userRepository.findAll().get(0);
        User datfeel = userRepository.findAll().get(1);
        Movie sobadMovie = movieRepository.findAll().get(0);
        Movie datfeelMovie = movieRepository.findAll().get(1);

        MockHttpServletRequestBuilder createSobadReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(sobadMovie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));


        MockHttpServletRequestBuilder createDatfeelReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(datfeelMovie.getId())
                                        .review("bad")
                                        .rating(0.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(createSobadReview);
        mockMvc.perform(createDatfeelReview);

        MockHttpServletRequestBuilder getAllRequest = get(REVIEW_CONTROLLER_PATH)
                .with(user("sobad").password("sobad"));

        ResultActions getAllResult = mockMvc.perform(getAllRequest).andExpect(status().isOk());

        String content = getAllResult.andReturn().getResponse().getContentAsString(UTF_8);

        ReviewPages allReviews = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(allReviews.getReviews()).hasSize(2);
        assertThat(allReviews.getReviews().get(0).getUser().getId()).isEqualTo(sobad.getId());
        assertThat(allReviews.getReviews().get(1).getUser().getId()).isEqualTo(datfeel.getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void updateReview() throws Exception {
        testUtils.createSampleUser();

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON);

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());
        Review review = reviewRepository.findAll().get(0);

        assertThat(review.getUserReview()).isNull();
        assertThat(review.getRating()).isNull();

        MockHttpServletRequestBuilder updateReviewRequest =
                put(REVIEW_CONTROLLER_PATH + "/" + review.getId())
                        .content(TestUtils.writeJson(
                                        ReviewDtoRequest.builder()
                                                .movieId(movie.getId())
                                                .review("good")
                                                .rating(5.0)
                                                .build()
                                )
                        )
                        .contentType(APPLICATION_JSON);

        ResultActions updateResult = mockMvc.perform(updateReviewRequest).andExpect(status().isOk());
        String content = updateResult.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewDto reviewDto = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(review.getUserReview()).isNotNull();
        assertThat(review.getRating()).isNotNull();
        assertThat(reviewDto.getReview()).isNotNull();
        assertThat(reviewDto.getRating()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void deleteReview() throws Exception {
        testUtils.createSampleUser();

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                        ReviewDtoRequest.builder()
                                .movieId(movie.getId())
                                .review("good")
                                .rating(5.0)
                                .build()
                        )
                )
                .contentType(APPLICATION_JSON);
        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());

        Review review = reviewRepository.findAll().get(0);
        MockHttpServletRequestBuilder deleteReviewRequest =
                delete(REVIEW_CONTROLLER_PATH + "/" + review.getId());

        mockMvc.perform(deleteReviewRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder getReviewByMovieAfterDelete = get(REVIEW_CONTROLLER_PATH)
                .param("movieId", String.valueOf(movie.getId()));

        ResultActions getResultAfterDelete = mockMvc.perform(getReviewByMovieAfterDelete).andExpect(status().isOk());
        String contentAfterDelete = getResultAfterDelete.andReturn().getResponse().getContentAsString(UTF_8);
        ReviewPages responseAfterDelete = TestUtils.readJson(contentAfterDelete, new TypeReference<>() { });

        assertThat(responseAfterDelete.getReviews()).isEmpty();
    }
    @Test
    @Transactional
    void deleteReviewFromAnotherUser() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        Movie movie = movieRepository.findAll().get(0);
        MockHttpServletRequestBuilder createReviewRequest = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(5.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());

        Review review = reviewRepository.findAll().get(0);
        MockHttpServletRequestBuilder deleteReviewRequest =
                delete(REVIEW_CONTROLLER_PATH + "/" + review.getId())
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(deleteReviewRequest).andExpect(status().isForbidden());

        assertThat(reviewRepository.findAll()).hasSize(1);
    }

}
