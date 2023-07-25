package sobad.code.movies_diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MoviePage;
import sobad.code.movies_diary.dtos.movie.MoviePageShort;
import sobad.code.movies_diary.dtos.movie.UserMovie;
import sobad.code.movies_diary.dtos.movie.UserMoviesPage;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.entiryExceptions.MovieNotFoundException;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.mappers.PageMapper;
import sobad.code.movies_diary.mappers.entitySerializers.MovieDtoSerializer;
import sobad.code.movies_diary.mappers.entitySerializers.MovieSerializer;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepositoryImpl;
import sobad.code.movies_diary.repositories.dsl.filters.GenreFilter;
import sobad.code.movies_diary.repositories.dsl.filters.TitleFilter;
import sobad.code.movies_diary.repositories.dsl.filters.UserIdFilter;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieSerializer movieSerializer;
    private final MovieDtoSerializer movieDtoSerializer;
    private final ExternalApiService externalApiService;
    private final MovieMapper movieMapper;
    private final PageMapper pageMapper;
    private final UserService userService;

    public MovieDto getMovieById(Long id) {
        Optional<Movie> movieInDb =  movieRepository.findById(id);
        if (movieInDb.isEmpty()) {
            throw new MovieNotFoundException(String.format("Фильм с данным id '%s' не найден", id));
        }
        Movie movie = movieInDb.get();

        return movieDtoSerializer.apply(movie);
    }

    @Transactional
    public MoviePage getMoviesByName(String name, Boolean findOnKp, Integer page, Integer limit) {
        if (findOnKp) {
            MoviePage kpMovies = externalApiService.findMovieByName(name, page, limit);

            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();

            movieRepository.saveAll(movies);

            return kpMovies;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByTitleFilter(new TitleFilter(name), pageRequest);

        return pageMapper.buildMoviePage(limit, page, moviePage);
    }

    @Transactional
    public MoviePageShort getMoviesByNameShortInfo(String name, Boolean findOnKp, Integer page, Integer limit) {
        if (findOnKp) {
            MoviePage kpMovies = externalApiService.findMovieByName(name, page, limit);

            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();

            movieRepository.saveAll(movies);
            return pageMapper.buildMoviePageShortFromKp(limit, page, kpMovies, movies);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByTitleFilter(new TitleFilter(name), pageRequest);

        return pageMapper.buildMoviePageShort(limit, page, moviePage);
    }

    public MoviePage getMoviesByGenre(String genreName, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByGenreNameFilter(new GenreFilter(genreName), pageRequest);

        return pageMapper.buildMoviePage(limit, page, moviePage);
    }

    public UserMoviesPage getMoviesByUser(Long userId, Integer page, Integer limit) {
        User user = userService.findById(userId);
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> movies = movieCustomRepository.findByUserIdFilter(new UserIdFilter(user.getId()), pageRequest);

        List<UserMovie> userMovies = movies.getContent().stream()
                .map(e -> movieMapper.toUserMovieDto(e, userId))
                .toList();

        return UserMoviesPage.builder()
                .userId(userId)
                .username(user.getUsername())
                .movies(userMovies)
                .total(movies.getTotalElements())
                .page(page)
                .limit(limit)
                .pages(movies.getTotalPages())
                .build();
    }

}
