package sobad.code.movies_diary.controllers;

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
import sobad.code.movies_diary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.movies_diary.dtos.GenreDto;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MovieDtoShort;
import sobad.code.movies_diary.dtos.movie.MoviePages;
import sobad.code.movies_diary.dtos.movie.MoviePagesShort;
import sobad.code.movies_diary.dtos.movie.UserMoviesPage;
import sobad.code.movies_diary.dtos.review.ReviewDtoRequest;
import sobad.code.movies_diary.entities.Genre;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.GenreRepository;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.utils.TestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH_GENRE;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH_USERS;
import static sobad.code.movies_diary.controllers.ReviewController.REVIEW_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Slf4j
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
                        .genres(genres1)
                        .build()
        );
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieInAppShortInfo() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH)
                .param("title", "Человек-паук");

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePagesShort moviePageShort = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviePageShort.getLimit()).isEqualTo(10);
        assertThat(moviePageShort.getPage()).isEqualTo(1);
        assertThat(moviePageShort.getMovies().get(0)).isInstanceOf(MovieDtoShort.class);

        Long id = moviePageShort.getMovies().get(0).getId();
        Optional<Movie> movie = movieRepository.findById(id);

        assertThat(movie).isPresent();
        assertThat(content).doesNotContain(movie.get().getDescription());
        assertThat(movie.get().getTitle()).isEqualTo(moviePageShort.getMovies().get(0).getTitle());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieAppFullInfo() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH)
                .param("title", "Человек-паук")
                .param("expanded", "true");

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviePage.getLimit()).isEqualTo(10);
        assertThat(moviePage.getPage()).isEqualTo(1);
        assertThat(moviePage.getMovies().get(0)).isInstanceOf(MovieDto.class);


        Long id = moviePage.getMovies().get(0).getId();
        Optional<Movie> movie = movieRepository.findById(id);

        assertThat(movie).isPresent();
        assertThat(content).contains(movie.get().getDescription());
        assertThat(movie.get().getTitle()).isEqualTo(moviePage.getMovies().get(0).getTitle());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieAppPage() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH)
                .param("title", "Человек-паук")
                .param("expanded", "true")
                .param("limit", "1")
                .param("page", "2");

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviePage.getLimit()).isEqualTo(1);
        assertThat(moviePage.getPage()).isEqualTo(2);
        assertThat(moviePage.getTotal()).isEqualTo(5);
        assertThat(moviePage.getMovies().get(0)).isInstanceOf(MovieDto.class);

        List<Movie> movies = movieRepository.findAll();
        Movie movie = movies.get(0);

        assertThat(movie.getId()).isNotEqualTo(moviePage.getMovies().get(0).getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findNonExistedMovieAppPage() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH)
                .param("title", "qwer");

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviePage.getLimit()).isEqualTo(10);
        assertThat(moviePage.getPage()).isEqualTo(1);
        assertThat(moviePage.getTotal()).isEqualTo(0);

        assertThat(moviePage.getMovies().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieAppGenrePage() throws Exception {
        testUtils.createSampleUser();

        String genreName = "боевик";
        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH_GENRE)
                .param("genreName", genreName);

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(content, new TypeReference<>() { });
        GenreDto existedGenre = new GenreDto(genreName);
        GenreDto nonExistedGenre = new GenreDto("123");

        moviePage.getMovies()
                .forEach(movie -> {
                    Set<GenreDto> genres = movie.getGenres();
                    assertThat(genres).contains(existedGenre);
                });

        moviePage.getMovies()
                .forEach(movie -> {
                    Set<GenreDto> genres = movie.getGenres();
                    assertThat(genres).doesNotContain(nonExistedGenre);
                });

        assertThat(moviePage.getTotal()).isEqualTo(4);
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieAppByUser() throws Exception {
        testUtils.createSampleUser();

        Movie movie = movieRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder requestMoviesByUsername = get(
                MOVIE_CONTROLLER_PATH_USERS + "/{userId}", user.getId());

        ResultActions result = mockMvc.perform(requestMoviesByUsername).andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        UserMoviesPage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies().size()).isEqualTo(0);

        ReviewDtoRequest reviewDtoRequest = new ReviewDtoRequest(movie.getId(), "good", 10.0);

        MockHttpServletRequestBuilder requestCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(reviewDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateReview).andExpect(status().isCreated());

        result = mockMvc.perform(requestMoviesByUsername).andExpect(status().isOk());

        content = result.andReturn().getResponse().getContentAsString(UTF_8);

        response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies().size()).isEqualTo(1);
        assertThat(response.getMovies().get(0).getId()).isEqualTo(movie.getId());
        assertThat(response.getMovies().get(0).getReview()).isEqualTo(reviewRepository.findAll().get(0).getReview());
        assertThat(response.getMovies().get(0).getRating()).isEqualTo(reviewRepository.findAll().get(0).getRating());
    }

    @Test
    @Transactional
    void findMovieAppByDifferentUser() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        Movie movie1 = movieRepository.findAll().get(0);
        Movie movie2 = movieRepository.findAll().get(1);
        User sobad = userRepository.findAll().get(0);
        User datfeel = userRepository.findAll().get(1);

        ReviewDtoRequest sobadReview = new ReviewDtoRequest(movie1.getId(), "good", 10.0);

        MockHttpServletRequestBuilder sobadCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(sobadReview))
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));

        mockMvc.perform(sobadCreateReview).andExpect(status().isCreated());

        ReviewDtoRequest datfeelReview = new ReviewDtoRequest(movie2.getId(), "bad", 0.0);

        MockHttpServletRequestBuilder datfeelCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(datfeelReview))
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        mockMvc.perform(datfeelCreateReview).andExpect(status().isCreated());

        MockHttpServletRequestBuilder sobadMovies = get(
                MOVIE_CONTROLLER_PATH_USERS + "/{userId}", sobad.getId());

        MockHttpServletRequestBuilder datfeelMovies = get(
                MOVIE_CONTROLLER_PATH_USERS + "/{userId}", datfeel.getId());

        ResultActions sobadResult = mockMvc.perform(sobadMovies).andExpect(status().isOk());
        ResultActions datfeelResult = mockMvc.perform(datfeelMovies).andExpect(status().isOk());

        String sobadContent = sobadResult.andReturn().getResponse().getContentAsString(UTF_8);
        String datfeelContent = datfeelResult.andReturn().getResponse().getContentAsString(UTF_8);

        UserMoviesPage sobadResponse = TestUtils.readJson(sobadContent, new TypeReference<>() { });
        UserMoviesPage datfeelResponse = TestUtils.readJson(datfeelContent, new TypeReference<>() { });

        assertThat(sobadResponse.getMovies().get(0).getId()).isEqualTo(movie1.getId());
        assertThat(datfeelResponse.getMovies().get(0).getId()).isEqualTo(movie2.getId());
        assertThat(sobadResponse.getMovies().size()).isEqualTo(1);
        assertThat(datfeelResponse.getMovies().size()).isEqualTo(1);
        assertThat(sobadResponse.getMovies().get(0).getRating())
                .isEqualTo(reviewRepository.findAll().get(0).getRating());
        assertThat(datfeelResponse.getMovies().get(0).getRating())
                .isEqualTo(reviewRepository.findAll().get(1).getRating());
    }
    @Test
    @Transactional
    void averageRatingInclude() throws Exception {
        testUtils.createSampleUser();
        testUtils.createAnotherUser();

        UserRegistrationDtoRequest user3 = UserRegistrationDtoRequest.builder()
                .username("user3")
                .email("user3@mail.com")
                .password("s")
                .confirmPassword("s")
                .build();
        UserRegistrationDtoRequest user4 = UserRegistrationDtoRequest.builder()
                .username("user4")
                .email("user4@mail.com")
                .password("s")
                .confirmPassword("s")
                .build();
        UserRegistrationDtoRequest user5 = UserRegistrationDtoRequest.builder()
                .username("user5")
                .email("user5@mail.com")
                .password("s")
                .confirmPassword("s")
                .build();

        testUtils.createUser(user3);
        testUtils.createUser(user4);
        testUtils.createUser(user5);

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder createReviewRequest1 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(1.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("sobad").password("sobad"));

        MockHttpServletRequestBuilder createReviewRequest2 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(3.5)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        MockHttpServletRequestBuilder createReviewRequest3 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(6.5)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("user3").password("user3"));

        MockHttpServletRequestBuilder createReviewRequest4 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(7.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("user4").password("user4"));

        MockHttpServletRequestBuilder createReviewRequest5 = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(
                                ReviewDtoRequest.builder()
                                        .movieId(movie.getId())
                                        .review("good")
                                        .rating(10.0)
                                        .build()
                        )
                )
                .contentType(APPLICATION_JSON)
                .with(user("user5").password("user5"));

        mockMvc.perform(createReviewRequest1);
        mockMvc.perform(createReviewRequest2);
        mockMvc.perform(createReviewRequest3);
        mockMvc.perform(createReviewRequest4);
        mockMvc.perform(createReviewRequest5);

        MockHttpServletRequestBuilder requestFindMovieInApp = get(MOVIE_CONTROLLER_PATH)
                .param("title", "Человек-паук")
                .param("expanded", "true")
                .param("limit", "1")
                .with(user("sobad").password("sobad"));

        ResultActions findResult = mockMvc.perform(requestFindMovieInApp).andExpect(status().isOk());
        String appResultContent = findResult.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(appResultContent, new TypeReference<>() { });
        assertThat(moviePage.getMovies().get(0).getAverageRating()).isNotNull();
    }
}
