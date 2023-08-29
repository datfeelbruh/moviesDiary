package sobad.code.moviesdiary.it;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.ConfigForTests;
import sobad.code.moviesdiary.dtos.movie.MovieCard;
import sobad.code.moviesdiary.dtos.movie.MovieTitlesId;
import sobad.code.moviesdiary.dtos.GenreDto;
import sobad.code.moviesdiary.dtos.pages.MoviePages;
import sobad.code.moviesdiary.dtos.pages.UserMoviesPage;
import sobad.code.moviesdiary.dtos.review.ReviewDtoRequest;
import sobad.code.moviesdiary.entities.Genre;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.repositories.GenreRepository;
import sobad.code.moviesdiary.repositories.MovieRepository;
import sobad.code.moviesdiary.repositories.ReviewRepository;
import sobad.code.moviesdiary.repositories.UserRepository;
import sobad.code.moviesdiary.utils.TestUtils;

import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.moviesdiary.ConfigForTests.TEST_PROFILE;
import static sobad.code.moviesdiary.controllers.MovieController.MOVIE_CONTROLLER_PATH;
import static sobad.code.moviesdiary.controllers.MovieController.MOVIE_CONTROLLER_PATH_GENRE;
import static sobad.code.moviesdiary.controllers.MovieController.MOVIE_CONTROLLER_PATH_USERS;
import static sobad.code.moviesdiary.controllers.ReviewController.REVIEW_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigForTests.class)
@ExtendWith(SpringExtension.class)
@Slf4j
@ActiveProfiles(TEST_PROFILE)
class MovieControllerIT {
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
    void getMovieById() throws Exception {
        testUtils.createSampleUser();
        User user = userRepository.findAll().get(0);

        Movie movie = movieRepository.findAll().get(0);

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH + "/" + movie.getId());

        ResultActions result = testUtils.performWithToken(requestFind, user).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MovieCard movieCard = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(movieCard).isNotNull();
        assertThat(movieCard.getId()).isEqualTo(movie.getId());
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void getMovieTitles() throws Exception {
        testUtils.createSampleUser();

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH + "/moviesTitles");

        ResultActions result = mockMvc.perform(requestFind).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        List<MovieTitlesId> moviesTitles = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviesTitles.get(0).getId()).isNotNull();
        assertThat(moviesTitles.get(0).getTitle()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findNonExistedMovieAppPage() throws Exception {
        testUtils.createSampleUser();
        User user = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH)
                .param("title", "qwer");

        ResultActions result = testUtils.performWithToken(requestFind, user).andExpect(status().isOk());

        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        MoviePages moviePage = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(moviePage.getLimit()).isEqualTo(10);
        assertThat(moviePage.getPage()).isEqualTo(1);
        assertThat(moviePage.getTotal()).isZero();

        assertThat(moviePage.getMovies()).isEmpty();
    }

    @Test
    @Transactional
    @WithMockUser("sobad")
    void findMovieAppGenrePage() throws Exception {
        testUtils.createSampleUser();
        User user = userRepository.findAll().get(0);

        String genreName = "боевик";
        MockHttpServletRequestBuilder requestFind = get(MOVIE_CONTROLLER_PATH_GENRE)
                .param("genreName", genreName);

        ResultActions result = testUtils.performWithToken(requestFind, user).andExpect(status().isOk());
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
                    assertThat(genres).isNotEmpty();
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

        ResultActions result = testUtils.performWithToken(requestMoviesByUsername, user).andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString(UTF_8);
        UserMoviesPage response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies()).isEmpty();

        ReviewDtoRequest reviewDtoRequest = new ReviewDtoRequest(movie.getId(), "good", 10.0);

        MockHttpServletRequestBuilder requestCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(reviewDtoRequest))
                .contentType(APPLICATION_JSON);

        testUtils.performWithToken(requestCreateReview, user).andExpect(status().isCreated());

        result = mockMvc.perform(requestMoviesByUsername).andExpect(status().isOk());

        content = result.andReturn().getResponse().getContentAsString(UTF_8);

        response = TestUtils.readJson(content, new TypeReference<>() { });

        assertThat(response.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getMovies()).hasSize(1);
        assertThat(response.getMovies().get(0).getId()).isEqualTo(movie.getId());
        assertThat(response.getMovies().get(0).getReview())
                .isEqualTo(reviewRepository.findAll().get(0).getUserReview());
        assertThat(response.getMovies().get(0).getRating())
                .isEqualTo(reviewRepository.findAll().get(0).getRating());
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

        testUtils.performWithToken(sobadCreateReview, sobad).andExpect(status().isCreated());

        ReviewDtoRequest datfeelReview = new ReviewDtoRequest(movie2.getId(), "bad", 0.0);

        MockHttpServletRequestBuilder datfeelCreateReview = post(REVIEW_CONTROLLER_PATH)
                .content(TestUtils.writeJson(datfeelReview))
                .contentType(APPLICATION_JSON)
                .with(user("datfeel").password("datfeel"));

        testUtils.performWithToken(datfeelCreateReview, datfeel).andExpect(status().isCreated());

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
        assertThat(sobadResponse.getMovies()).hasSize(1);
        assertThat(datfeelResponse.getMovies()).hasSize(1);
        assertThat(sobadResponse.getMovies().get(0).getRating())
                .isEqualTo(reviewRepository.findAll().get(0).getRating());
        assertThat(datfeelResponse.getMovies().get(0).getRating())
                .isEqualTo(reviewRepository.findAll().get(1).getRating());
    }
}
