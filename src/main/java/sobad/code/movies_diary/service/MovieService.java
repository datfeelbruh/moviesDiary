package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.MovieShortInfo;
import sobad.code.movies_diary.dto.movie.UserMovies;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.MovieNotFoundException;
import sobad.code.movies_diary.mappers.ExternalAPIShortInfoSerializer;
import sobad.code.movies_diary.mappers.MovieCardSerializer;
import sobad.code.movies_diary.mappers.MovieEntityShortSerializer;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.mappers.MovieEntitySerializer;
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
    private final MovieEntitySerializer movieEntitySerializer;
    private final MovieEntityShortSerializer movieEntityShortSerializer;
    private final MovieCardSerializer movieCardResponseSerializer;
    private final ExternalApiService externalApiService;
    private final ExternalAPIShortInfoSerializer externalAPIShortInfoSerializer;
    private final MovieMapper movieMapper;

    public MovieCard getMovieById(Long id) {
        Optional<Movie> movieInDb =  movieRepository.findById(id);
        if (movieInDb.isEmpty()) {
            throw new MovieNotFoundException(String.format("Фильм с данным id '%s' не найден", id));
        }
        Movie movie = movieInDb.get();

        return movieEntitySerializer.apply(movie);
    }

    public List<MovieCard> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieEntitySerializer)
                .toList();
    }

    @Transactional
    public List<MovieCard> getMoviesByName(String name, Boolean findOnKp) {
        if (findOnKp) {
            List<MovieCard> kpMovies = externalApiService.findMovieByName(name);

            List<Movie> movies = kpMovies.stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieCardResponseSerializer)
                    .toList();

            movieRepository.saveAll(movies);
            return kpMovies;
        }

        return movieCustomRepository.findByMovieNameFilter(new MovieNameFilter(name))
                .stream()
                .map(movieEntitySerializer)
                .toList();
    }

    @Transactional
    public List<MovieShortInfo> getMoviesByNameShortInfo(String name, Boolean findOnKp) {
        if (findOnKp) {
            List<MovieCard> kpMovies = externalApiService.findMovieByName(name);

            List<Movie> movies = kpMovies.stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieCardResponseSerializer)
                    .toList();

            movieRepository.saveAll(movies);

            return kpMovies.stream()
                    .map(externalAPIShortInfoSerializer)
                    .toList();
        }

        return movieCustomRepository.findByMovieNameFilter(new MovieNameFilter(name))
                .stream()
                .map(movieEntityShortSerializer)
                .toList();
    }

    public List<MovieCard> getMoviesByGenre(String genreName) {
        return movieCustomRepository.findByFilter(new MovieGenreFilter(genreName))
                .stream()
                .map(movieEntitySerializer)
                .toList();
    }

    public List<MovieShortInfo> getMoviesByGenreShortInfo(String genreName) {
        return movieCustomRepository.findByFilter(new MovieGenreFilter(genreName))
                .stream()
                .map(movieEntityShortSerializer)
                .toList();
    }

    public UserMovies getMoviesByUser(String username) {
        User user = userService.findByUsername(username);
        List<Movie> movies = movieCustomRepository.findByMovieUserIdFilter(new MovieUserIdFilter(user.getId()));

        return movieMapper.mapFromEntityToUserMovieResponse(movies, user);
    }

}
