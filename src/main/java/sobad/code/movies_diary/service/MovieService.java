package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.repositories.dsl.filters.MovieGenreFilter;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepositoryImpl;
import sobad.code.movies_diary.repositories.dsl.filters.MovieNameFilter;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieRepository movieRepository;
    private final UserService userService;
    private final MovieRatingService movieRatingService;
    private final MovieMapper movieMapper;

    public MovieDtoResponse createMovie(MovieDtoRequest movieDtoRequest) {
        Optional<Movie> movieInDb = movieRepository.findByKpId(movieDtoRequest.getKpId());
        if (movieInDb.isPresent()) {
            Movie movie = movieInDb.get();
            return buildResponse(movie, movieDtoRequest);
        }

        Movie movie = movieMapper.mapFromRequestDto(movieDtoRequest);
        movieRepository.save(movie);

        return buildResponse(movie, movieDtoRequest);
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

    public List<MovieDtoResponse> getMoviesByName(String movieName) {
        return movieCustomRepository.findByMovieNameFilter(new MovieNameFilter(movieName))
                .stream()
                .map(movieMapper::mapFromEntityToResponseDto)
                .peek(e -> {
                    e.setAverageRating(movieRatingService.calcAverageRating(e.getId()).orElseThrow());
                })
                .toList();
    }

    public List<MovieDtoResponse> getMoviesListByGenre(String genreName) {
        return movieCustomRepository.findByFilter(new MovieGenreFilter(genreName))
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
        response.setUserRating(movieRatingService.getRatingById(
                response.getId(), userService.getCurrentUser().getId())
        );
        response.setAverageRating(movieRatingService.calcAverageRating(
                response.getId()).orElse(request.getUserRating())
        );

        return response;
    }
}
