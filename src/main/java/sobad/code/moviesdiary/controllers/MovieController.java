package sobad.code.moviesdiary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.movie.PopularMovieDto;
import sobad.code.moviesdiary.dtos.movie.MovieCard;
import sobad.code.moviesdiary.dtos.movie.MovieTitlesId;
import sobad.code.moviesdiary.dtos.pages.FavoritesMoviesPage;
import sobad.code.moviesdiary.dtos.pages.MoviePages;
import sobad.code.moviesdiary.dtos.pages.PageDto;
import sobad.code.moviesdiary.dtos.pages.UserMoviesPage;
import sobad.code.moviesdiary.services.MovieService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с фильмами", description = "API Фильмов")
public class MovieController {
    private final MovieService movieService;

    public static final String MOVIE_CONTROLLER_PATH = "/api/movie";
    public static final String MOVIE_CONTROLLER_PATH_USERS = "/api/movie/user";
    public static final String MOVIE_CONTROLLER_PATH_GENRE = "/api/movie/genre";

    @Operation(summary = "Получить фильм по ID из базы данных приложения")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Фильм с данным ID",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MovieCard.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Фильм с таким ID не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/{movieId}")
    public ResponseEntity<MovieCard> getMovieById(@PathVariable("movieId")
                                          @Parameter(description = "ID фильма", example = "1") Long movieId,
                                          @RequestParam(required = false, value = "findKp", defaultValue = "false")
                                          @Parameter(description = "Поиск по кинопоиску. true - искать на кинопоиске, "
                                                  + "false - в базе приложения.") Boolean findKp) {
        MovieCard movie = movieService.getMovieById(movieId, findKp);
        return new ResponseEntity<>(movie, OK);
    }


    @Operation(summary = "Получить все фильмы пользователя", description =
            """
            Возвращается все фильмы пользователя которые он добавил себе в коллекцию вместе его с ревью и оценкой.
            \s
            Метод возвращает определенное количество результатов сформированных в страницы на основе параметров.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Все фильмы из базы данных которые закреплены за пользователем",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserMoviesPage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Пользователь не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH_USERS + "/{userId}")
    public ResponseEntity<UserMoviesPage> getMoviesByUser(
            @PathVariable(value = "userId") @Parameter(description = "ID пользователя",
                    example = "1") Long userId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit) {

        UserMoviesPage movies = movieService.getMoviesByUser(userId, page, limit);
        return new ResponseEntity<>(movies, OK);
    }

    @Operation(summary = "Добавить фильм в избранное")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Данные фильма добавленного в избранное",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MovieCard.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Фильм с таким ID не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
        )
    })
    @PostMapping(value = MOVIE_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<MovieCard> addToFavorite(@RequestParam
                                                   @Parameter(description = "ID фильма", example = "1") Long movieId) {
        MovieCard movieCard = movieService.addToFavorite(movieId);
        return new ResponseEntity<>(movieCard, OK);
    }

    @Operation(summary = "Получить список избранных фильмов")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список фильмов из избранного пользователя",
            content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = MovieCard.class))
                    )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<FavoritesMoviesPage> getFavorites(
            @RequestParam
            @Parameter(description = "ID пользователя", example = "1") Long userId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit) {
        FavoritesMoviesPage movies = movieService.getFavorites(userId, page, limit);
        return new ResponseEntity<>(movies, OK);
    }

    @Operation(summary = "Удалить фильм из избранного")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Фильм с таким ID удален из избранного",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Фильм с таким ID не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            )
    })
    @DeleteMapping(value = MOVIE_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<ResponseMessage> deleteFromFavorite(@RequestParam
                                   @Parameter(description = "ID фильма", example = "1") Long movieId) {
         return new ResponseEntity<>(movieService.deleteFromFavorites(movieId), OK);
    }

    @Operation(summary = "Универсальный поиск фильмов", description =
            """
           В этом методе можно составить запрос на получение фильма из базы приложения или через Кинопоиск API.
           \s
           Метод возвращает определенное количество результатов сформированных в страницы на основе параметров.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Фильмы соответствующие переданным параметрам",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MoviePages.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH)
    public ResponseEntity<PageDto> getMoviesByName(
            @RequestParam(value = "title") @Parameter(description = "Название фильма.",
                    example = "Разрушение") String title,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit,
            @RequestParam(required = false, value = "expanded", defaultValue = "false")
            @Parameter(description = "Предоставить полную информацию о фильмах."
                    + "true - вернуть полную информацию о фильмах, "
                    + "false - только то что нужно для выпадающего поиска.") Boolean expanded,
            @RequestParam(required = false, value = "findKp", defaultValue = "false")
            @Parameter(description = "Поиск по кинопоиску. true - искать на кинопоиске, "
                    + "false - в базе приложения.") Boolean findKp) {

        if (Boolean.TRUE.equals(expanded)) {
            return new ResponseEntity<>(movieService.getMoviesByName(title, findKp, page, limit), OK);
        }
        return new ResponseEntity<>(movieService.getMoviesByNameShortInfo(title, findKp, page, limit), OK);
    }

    @Operation(summary = "Получить фильмы по жанру.", description =
            """
            Возвращается все фильмы представители переданного жанра.
            \s
            Метод возвращает определенное количество результатов сформированных в страницы на основе параметров.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Все фильмы из базы данных которые являются представителями переданного жанра.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MoviePages.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH_GENRE)
    public ResponseEntity<MoviePages> findMoviesByGenre(
            @RequestParam(value = "genreName") @Parameter(description = "Жанр фильма.",
                    example = "Боевик.") String genreName,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit) {

        return new ResponseEntity<>(movieService.getMoviesByGenre(genreName, page, limit), OK);
    }

    @Operation(summary = "Получить названия фильмов для фичи в поисковой строке.")
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/moviesTitles")
    public ResponseEntity<List<MovieTitlesId>> findMoviesTitles() {
        return new ResponseEntity<>(movieService.getMoviesName(), OK);
    }

    @Operation(summary = "Получить 5 фильмов с самым большим количеством ревью.", description =
            """
            Возвращается 5 фильмов с самым большим количеством ревью.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Фильмы из базы данных с самым большим количеством ревью.",
            content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = PopularMovieDto.class))
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/popular")
    public ResponseEntity<List<PopularMovieDto>> getPopularMovies(@RequestParam(required = false, defaultValue = "4")
                                                                      Integer count) {
        return new ResponseEntity<>(movieService.getPopularMovies(count), OK);
    }

}
