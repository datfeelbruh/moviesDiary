package sobad.code.moviesdiary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.movie.MovieDto;
import sobad.code.moviesdiary.dtos.movie.PopularMovieDto;
import sobad.code.moviesdiary.dtos.movie.MovieCard;
import sobad.code.moviesdiary.dtos.movie.MovieTitlesId;
import sobad.code.moviesdiary.dtos.pages.FavoritesMoviesPage;
import sobad.code.moviesdiary.dtos.pages.MoviePages;
import sobad.code.moviesdiary.dtos.pages.MoviePagesShort;
import sobad.code.moviesdiary.dtos.movie.UserMovie;
import sobad.code.moviesdiary.dtos.pages.UserMoviesPage;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.mappers.MovieMapper;
import sobad.code.moviesdiary.mappers.PageMapper;
import sobad.code.moviesdiary.mappers.entity_serializers.MovieSerializer;
import sobad.code.moviesdiary.repositories.MovieRepository;
import sobad.code.moviesdiary.repositories.MovieCustomRepositoryImpl;
import sobad.code.moviesdiary.repositories.ReviewCustomRepositoryImpl;
import sobad.code.moviesdiary.repositories.filters.GenreFilter;
import sobad.code.moviesdiary.repositories.filters.UserIdFilter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieSerializer movieSerializer;
    private final ExternalApiService externalApiService;
    private final MovieMapper movieMapper;
    private final PageMapper pageMapper;
    private final UserService userService;
    private final ReviewCustomRepositoryImpl reviewCustomRepository;

    @Transactional
    public MovieCard addToFavorite(Long movieId) {
        User user = userService.getCurrentUser();
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Фильм с данным ID: '%s' не найден!", movieId)));
        user.getFavorites().add(movie);
        userService.updateUser(user);

        return movieMapper.toMovieCard(movie, user.getId());
    }
    @Transactional
    public FavoritesMoviesPage getFavorites(Long userId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviesPage = userService.getUserFavorites(userId, pageRequest);

        List<MovieCard> movies = moviesPage.getContent().stream()
                .map(e -> movieMapper.toMovieCard(e, userId))
                .toList();

        FavoritesMoviesPage moviePages = new FavoritesMoviesPage(movies);
        moviePages.setPage(page);
        moviePages.setPages(moviesPage.getTotalPages());
        moviePages.setTotal(moviesPage.getTotalElements());
        moviePages.setLimit(limit);

        return moviePages;
    }

    public ResponseMessage deleteFromFavorites(Long movieId) {
        User user = userService.getCurrentUser();
        Set<Movie> updatedFavorites = user.getFavorites().stream()
                .filter(e -> !e.getId().equals(movieId))
                .collect(Collectors.toSet());

        user.setFavorites(updatedFavorites);
        userService.updateUser(user);

        return ResponseMessage.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Фильм с ID: '%s' успешно удален из избранного.", movieId))
                .timestamp(Instant.now().toString())
                .build();
    }

    public MovieCard getMovieById(Long id, boolean findKp) {
        User user = userService.getCurrentUser();
        Optional<Movie> movieInDb =  movieRepository.findById(id);
        if (findKp && movieInDb.isEmpty()) {
            MoviePages kpMovies = externalApiService.findMovieById(id);
            Movie movie = movieSerializer.apply(kpMovies.getMovies().get(0));
            movieRepository.save(movie);
            return movieMapper.toMovieCard(movie, user.getId());
        }
        Movie movie = movieInDb.orElseThrow(() ->
                new EntityNotFoundException(String.format("Фильм с данным ID: '%s' не найден!", id)));

        return movieMapper.toMovieCard(movie, user.getId());
    }

    @Transactional
    public MoviePages getMoviesByName(String name, Boolean findOnKp, Integer page, Integer limit) {
        if (Boolean.TRUE.equals(findOnKp)) {
            MoviePages kpMovies = externalApiService.findMovieByName(name, page, limit);
            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();

            movieRepository.saveAll(movies);

            return kpMovies;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.searchBy(name, pageRequest, "title");
        return pageMapper.buildMoviePage(limit, page, moviePage);
    }

    @Transactional
    public MoviePagesShort getMoviesByNameShortInfo(String name, Boolean findOnKp, Integer page, Integer limit) {
        if (Boolean.TRUE.equals(findOnKp)) {
            MoviePages kpMovies = externalApiService.findMovieByName(name, page, limit);

            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();
            movieRepository.saveAll(movies);
            return pageMapper.buildMoviePageShortFromKp(limit, page, kpMovies, movies);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.searchBy(name, pageRequest, "title");
        return pageMapper.buildMoviePageShort(limit, page, moviePage);
    }

    @Transactional
    public MoviePages getMoviesByGenre(String genreName, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByGenreNameFilter(new GenreFilter(genreName), pageRequest);

        return pageMapper.buildMoviePage(limit, page, moviePage);
    }

    @Transactional
    public UserMoviesPage getMoviesByUser(Long userId, Integer page, Integer limit) {
        User user = userService.findById(userId);
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> movies = movieCustomRepository.findByUserIdFilter(new UserIdFilter(user.getId()), pageRequest);

        List<UserMovie> userMovies = movies.getContent().stream()
                .map(e -> movieMapper.toUserMovieDto(e, userId))
                .toList();

        UserMoviesPage userMoviesPage = UserMoviesPage.builder()
                .user(userService.getUserById(userId))
                .movies(userMovies)
                .build();
        userMoviesPage.setTotal(movies.getTotalElements());
        userMoviesPage.setPages(movies.getTotalPages());
        userMoviesPage.setPage(page);
        userMoviesPage.setLimit(limit);

        return userMoviesPage;
    }

    public List<MovieTitlesId> getMoviesName() {
        Map<Long, String> movies = movieCustomRepository.getTitlesWithId();
        List<MovieTitlesId> response = new ArrayList<>();
        movies.forEach((k, v) -> {
            MovieTitlesId movie = new MovieTitlesId(k, v);
            response.add(movie);
        });
        return response;
    }


    public List<PopularMovieDto> getPopularMovies(Integer count) {
        return reviewCustomRepository.getPopularMovies(count);
    }

}
