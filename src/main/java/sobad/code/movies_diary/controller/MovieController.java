package sobad.code.movies_diary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;

import sobad.code.movies_diary.service.MovieService;
import sobad.code.movies_diary.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addMovie(@RequestBody MovieDtoRequest movieDtoRequest) {
        try {
            MovieDtoResponse movie = movieService.addMovie(movieDtoRequest);
            return new ResponseEntity<>(movie, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            String.format(
                                    "Данный фильм '%s' уже добавлен в ваш профиль",
                                    movieDtoRequest.getMovieName()
                            )),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findMoviesByUser(@PathVariable String username) {
        try {
            List<MovieDtoResponse> movies = movieService.getUserMoviesList(username);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(),
                            String.format(
                                    "Такого пользователя '%s' не найдено",
                                    username
                            )),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/movies")
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

    @GetMapping("")
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
