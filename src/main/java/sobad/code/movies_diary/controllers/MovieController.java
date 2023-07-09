package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;

import sobad.code.movies_diary.service.MovieService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с фильмами")
public class MovieController {
    private final MovieService movieService;

    public static final String MOVIE_CONTROLLER_CREATE_PATH = "/api/movies";
    public static final String MOVIE_CONTROLLER_USERNAME_MOVIES_PATH = "/api/movies/{username}";
    public static final String MOVIE_CONTROLLER_GENRES_PATH = "/api/movies/genres";

    public static final String MOVIE_CONTROLLER_ALL_MOVIES_PATH = "/api/movies/all";

    @Operation(summary = "Добавление фильма пользователю"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные добавленнного фильма " +
                    "для отображение на странице пользователя", content =
            @Content(schema =
            @Schema(implementation = MovieDtoResponse.class))
            )
    })
    @PostMapping(MOVIE_CONTROLLER_CREATE_PATH)
    public ResponseEntity<?> createMovie(@RequestBody MovieDtoRequest movieDtoRequest) {
        try {
            MovieDtoResponse movie = movieService.createMovie(movieDtoRequest);
            return new ResponseEntity<>(movie, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            String.format(
                                    "Данный фильм '%s' уже добавлен в ваш профиль",
                                    movieDtoRequest.getMovieName()
                            )),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Operation(summary = "Получение всех фильмов в базе приложения"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список фильмов в базе", content =
            @Content(schema =
            @Schema(implementation = MovieDtoResponse.class))
            )
    })
    @GetMapping(MOVIE_CONTROLLER_ALL_MOVIES_PATH)
    public ResponseEntity<?> findAllMovies() {
        try {
            List<MovieDtoResponse> movies = movieService.getAllMovies();
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            "Что то пошло не так"
                            ),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получение всех фильмов из базы по жанру"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список фильмов по жанру", content =
            @Content(schema =
            @Schema(implementation = MovieDtoResponse.class))
            )
    })
    @GetMapping(MOVIE_CONTROLLER_GENRES_PATH)
    public ResponseEntity<?> findMoviesByGenre(@RequestParam String genre) {
        List<MovieDtoResponse> movies = movieService.getMoviesListByGenre(genre);
        if (movies.isEmpty()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.OK.value(),
                            String.format(
                                    "Не найдено фильмов с данным жанром '%s'",
                                    genre
                            )),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

}
