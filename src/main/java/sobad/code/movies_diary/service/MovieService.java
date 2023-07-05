package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.MovieRating;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepository;
import sobad.code.movies_diary.repositories.dsl.filters.MovieFilter;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieRepository movieRepository;
    private final UserService userService;
    private final MovieRatingService movieRatingService;
    private final MovieMapper movieMapper;

    public MovieDtoResponse addMovie(MovieDtoRequest movieDtoRequest) {
        Optional<Movie> movieInDb = movieRepository.findByKpId(movieDtoRequest.getKpId());
        if (movieInDb.isPresent()) {
            Movie movie = movieInDb.get();
            return buildResponse(movie, movieDtoRequest);
        }

        Movie movie = movieMapper.mapFromRequestDto(movieDtoRequest);
        movieRepository.save(movie);

        return buildResponse(movie, movieDtoRequest);
    }

    public List<MovieDtoResponse> getUserMoviesList(String username) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException();
        }

        return user.get().getMovies()
                .stream()
                .map(movieMapper::mapFromEntityToResponseDto)
                .peek(e -> {
                    Double userRating = movieRatingService.getRatingById(e.getId(), user.get().getId());
                    e.setUserRating(userRating);
                    e.setAverageRating(movieRatingService.calcAverageRating(e.getId()).orElse(userRating));
                })
                .toList();
    }

    public List<MovieDtoResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::mapFromEntityToResponseDto)
                .peek(e -> {
                    e.setAverageRating(movieRatingService.calcAverageRating(e.getId()).orElseThrow());
                })
                .toList();

    }

    public List<MovieDtoResponse> getMoviesListByGenre(String genreName) {
        return movieCustomRepository.findByFilter(new MovieFilter(genreName))
                .stream()
                .map(movieMapper::mapFromEntityToResponseDto)
                .peek(e -> {
                    e.setAverageRating(movieRatingService.calcAverageRating(e.getId()).orElseThrow());
                })
                .toList();
    }

    private MovieDtoResponse buildResponse(Movie movie, MovieDtoRequest request) {
        movieRatingService.create(movie, userService.getCurrentUser(), request.getUserRating());
        userService.addMovieToUser(movie);

        MovieDtoResponse response = movieMapper.mapFromEntityToResponseDto(movie);
        response.setUserRating(movieRatingService.getRatingById(response.getId(), userService.getCurrentUser().getId()));
        response.setAverageRating(movieRatingService.calcAverageRating(response.getId()).orElse(request.getUserRating()));

        return response;
    }
}
