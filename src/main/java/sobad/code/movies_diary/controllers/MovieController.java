package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dto.MovieDtoRequest;

import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.dto.UserMovieDto;
import sobad.code.movies_diary.service.MovieService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с фильмами")
public class MovieController {
    private final MovieService movieService;

    public static final String MOVIE_CONTROLLER_PATH = "/api/movies";


    @RequestMapping(value = MOVIE_CONTROLLER_PATH, method = POST)
    public ResponseEntity<?> createMovie(@RequestBody MovieDtoRequest movieDtoRequest) {
        UserMovieDto movie = movieService.createMovie(movieDtoRequest);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @RequestMapping(value = MOVIE_CONTROLLER_PATH + "/{id}", method = GET)
    public ResponseEntity<?> getMovieById(@PathVariable("id") Long id) {
        MovieDtoResponse movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }


    @RequestMapping(value = MOVIE_CONTROLLER_PATH, method = GET)
    public ResponseEntity<?> getMoviesByName(@RequestParam(required = false) String name,
                                             @RequestParam(required = false, defaultValue = "false") Boolean findKp) {
        List<MovieDtoResponse> movies = movieService.getMoviesByName(name, findKp);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @RequestMapping(value = MOVIE_CONTROLLER_PATH, method = GET, params = "genre")
    public ResponseEntity<?> findMoviesByGenre(@RequestParam(required = false) String genre) {
        List<MovieDtoResponse> movies = movieService.getMoviesByGenre(genre);
        if (movies.isEmpty()) {}
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @RequestMapping(value = MOVIE_CONTROLLER_PATH + "/haha/{username}", method = GET)
    public ResponseEntity<?> findMoviesByUsername(@PathVariable("username") String username) {
        List<UserMovieDto> movies = movieService.getMoviesByUser(username);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

}
