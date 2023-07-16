package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.dto.movie.UserMoviesDtoResponse;
import sobad.code.movies_diary.service.MovieService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с фильмами")
public class MovieController {
    private final MovieService movieService;

    public static final String MOVIE_CONTROLLER_PATH = "/api/movies";

    @GetMapping(value = MOVIE_CONTROLLER_PATH + "/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable("id") Long id) {
        MovieDtoResponse movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, OK);
    }

    @GetMapping(value = MOVIE_CONTROLLER_PATH)
    public ResponseEntity<?> getAllMovies() {
        return new ResponseEntity<>(movieService.getAllMovies(), OK);
    }

    @GetMapping(
            value = MOVIE_CONTROLLER_PATH,
            params = "username"
    )
    public ResponseEntity<?> getMoviesByUser(@RequestParam(required = false, value = "username") String username) {
        UserMoviesDtoResponse movies = movieService.getMoviesByUser(username);
        return new ResponseEntity<>(movies, OK);
    }

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
            value = MOVIE_CONTROLLER_PATH,
            params = "genreName"
    )
    public ResponseEntity<?> findMoviesByGenre(
            @RequestParam(required = false, value = "genreName") String genreName,
            @RequestParam(required = false, value = "expanded", defaultValue = "false") Boolean expanded) {
        if (expanded) {
            return new ResponseEntity<>(movieService.getMoviesByGenre(genreName), OK);
        }
        return new ResponseEntity<>(movieService.getMoviesByGenreShortInfo(genreName), OK);
    }

}
