package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
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

import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.UserMovies;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.service.MovieService;

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
    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable("id") Long id) {
        MovieCard movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, OK);
    }

    @Operation(summary = "Получить все фильмы из базы данных приложения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все фильмы из базы данных",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MovieCard.class))
                            )
                    }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH)
    public ResponseEntity<?> getAllMovies() {
        return new ResponseEntity<>(movieService.getAllMovies(), OK);
    }

    @Operation(summary = "Получить все фильмы пользователя", description =
            """
            Возвращается все фильмы пользователя которые он добавил себе в коллекцию вместе его с ревью и оценкой.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все фильмы из базы данных которые закреплены за пользователем",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserMovies.class))
                            )
                    }
            )
    })
    @GetMapping(value = MOVIE_CONTROLLER_PATH_USERS)
    public ResponseEntity<?> getMoviesByUser(@RequestParam(value = "username") String username) {
        UserMovies movies = movieService.getMoviesByUser(username);
        return new ResponseEntity<>(movies, OK);
    }


    @Operation(summary = "Универсальный поиск фильмов", description =
            """
            Комбинации запросов данного эндпоинта:
            \s
            1) movieName(findKp = false) - все фильмы из базы данных приложения которые содержат
            переданную подстроку.
            \s
            2) movieName(findKp = true) - все фильмы которые содержат данную подстроку, поиск будет произведен через
            внешнее API Кинопоиска.
            \s
            3) genreName - все фильмы из базы данных приложения жанр которых соответствует переданному названию
            жанра.
            \s
            Параметр expanded = true - возвращает больше информации о найденных фильмах(оценки, ревью, etc).
            \s
            Допускаются комбинации - genreName&expanded и movieName + expanded&findKp.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильмы соответствующие переданным параметрам",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MovieCard.class))
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Некорректная комбинация параметров",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @GetMapping(
            value = MOVIE_CONTROLLER_PATH,
            params = "movieName"
    )
    public ResponseEntity<?> getMoviesByName(
            @RequestParam(required = false, value = "movieName") String movieName,
            @RequestParam(required = false, value = "findKp", defaultValue = "false") Boolean findKp,
            @RequestParam(required = false, value = "expanded",defaultValue = "false") Boolean expanded) {

        if (expanded) {
            return new ResponseEntity<>(movieService.getMoviesByName(movieName, findKp), OK);
        }
        return new ResponseEntity<>(movieService.getMoviesByNameShortInfo(movieName, findKp), OK);
    }

    @GetMapping(
            value = MOVIE_CONTROLLER_PATH_GENRE,
            params = "genreName"
    )
    public ResponseEntity<?> findMoviesByGenre(
            @RequestParam(value = "genreName") String genreName,
            @RequestParam(required = false, value = "expanded", defaultValue = "false") Boolean expanded) {
        if (expanded) {
            return new ResponseEntity<>(movieService.getMoviesByGenre(genreName), OK);
        }
        return new ResponseEntity<>(movieService.getMoviesByGenreShortInfo(genreName), OK);
    }

}
