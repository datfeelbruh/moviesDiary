package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.dto.movie.MovieDtoShortInfo;
import sobad.code.movies_diary.dto.movie.UserMoviesDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepositoryImpl;
import sobad.code.movies_diary.repositories.dsl.filters.MovieGenreFilter;
import sobad.code.movies_diary.repositories.dsl.filters.MovieNameFilter;
import sobad.code.movies_diary.repositories.dsl.filters.MovieUserIdFilter;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieRepository movieRepository;
    private final UserService userService;
    private final ExternalApiService externalApiService;
    private final MovieMapper movieMapper;

    public MovieDtoResponse getMovieById(Long id) {
        Optional<Movie> movieInDb =  movieRepository.findById(id);
        if (movieInDb.isEmpty()) {
            throw new RuntimeException("нет фильма соси");
        }
        Movie movie = movieInDb.get();

        return movieMapper.mapFromEntityToResponse(movie);
    }

    public List<MovieDtoResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::mapFromEntityToResponse)
                .toList();
    }

    public List<MovieDtoResponse> getMoviesByName(String name, Boolean findOnKp) {
        if (findOnKp) {
            List<MovieDtoResponse> kpMovies = externalApiService.findMovieByName(name);

            List<Movie> movies = kpMovies.stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieMapper::mapFromMovieInfoToEntity)
                    .toList();

            movieRepository.saveAll(movies);
            return kpMovies;
        }

        return movieCustomRepository.findByMovieNameFilter(new MovieNameFilter(name))
                .stream()
                .map(movieMapper::mapFromEntityToResponse)
                .toList();
    }

    public List<MovieDtoShortInfo> getMoviesByNameShortInfo(String name, Boolean findOnKp) {
        if (findOnKp) {
            List<MovieDtoResponse> kpMovies = externalApiService.findMovieByName(name);

            List<Movie> movies = kpMovies.stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieMapper::mapFromMovieInfoToEntity)
                    .toList();

            movieRepository.saveAll(movies);
            return kpMovies.stream()
                    .map(e -> MovieDtoShortInfo.builder()
                            .id(e.getId())
                            .title(e.getTitle())
                            .posterUrl(e.getPosterUrl())
                            .releaseYear(e.getReleaseYear())
                            .build()
                    )
                    .toList();
        }

        return movieCustomRepository.findByMovieNameFilter(new MovieNameFilter(name))
                .stream()
                .map(movieMapper::mapToShortInfo)
                .toList();
    }

    public List<MovieDtoResponse> getMoviesByGenre(String genreName) {
        return movieCustomRepository.findByFilter(new MovieGenreFilter(genreName))
                .stream()
                .map(movieMapper::mapFromEntityToResponse)
                .toList();
    }

    public List<MovieDtoShortInfo> getMoviesByGenreShortInfo(String genreName) {
        return movieCustomRepository.findByFilter(new MovieGenreFilter(genreName))
                .stream()
                .map(movieMapper::mapToShortInfo)
                .toList();
    }

    public UserMoviesDtoResponse getMoviesByUser(String username) {
        User user = userService.findByUsername(username).orElseThrow();
        List<Movie> movies = movieCustomRepository.findByMovieUserIdFilter(new MovieUserIdFilter(user.getId()));

        return movieMapper.mapFromEntityToUserMovieResponse(movies, user);
    }

}
