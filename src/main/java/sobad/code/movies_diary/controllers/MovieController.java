package sobad.code.movies_diary.controllers;

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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sobad.code.movies_diary.dtos.movie.MovieCard;
import sobad.code.movies_diary.dtos.movie.MoviePages;
import sobad.code.movies_diary.dtos.movie.UserMoviesPage;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.services.MovieService;

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
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/{movieId}")
    public ResponseEntity<?> getMovieById(@PathVariable("movieId")
                                          @Parameter(description = "ID фильма", example = "1") Long movieId) {
        MovieCard movie = movieService.getMovieById(movieId);
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
                        array = @ArraySchema(schema = @Schema(implementation = UserMoviesPage.class))
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Пользователь не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH_USERS + "/{userId}")
    public ResponseEntity<?> getMoviesByUser(
            @PathVariable(value = "userId") @Parameter(description = "ID пользователя",
                    example = "1") Long userId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit) {

        UserMoviesPage movies = movieService.getMoviesByUser(userId, page, limit);
        return new ResponseEntity<>(movies, OK);
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
                        array = @ArraySchema(schema = @Schema(implementation = MoviePages.class))
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH)
    public ResponseEntity<?> getMoviesByName(
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

        if (expanded) {
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
                        array = @ArraySchema(schema = @Schema(implementation = MoviePages.class))
                        )
                }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH_GENRE)
    public ResponseEntity<?> findMoviesByGenre(
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
    public ResponseEntity<?> findMoviesTitles() {
        return new ResponseEntity<>(movieService.getMoviesName(), OK);
    }
}
