package sobad.code.moviesdiary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.dtos.movie.MovieCard;
import sobad.code.moviesdiary.dtos.movie.MovieTitlesId;
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
import sobad.code.moviesdiary.repositories.dsl.MovieCustomRepositoryImpl;
import sobad.code.moviesdiary.repositories.dsl.filters.GenreFilter;
import sobad.code.moviesdiary.repositories.dsl.filters.TitleFilter;
import sobad.code.moviesdiary.repositories.dsl.filters.UserIdFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    public MovieCard getMovieById(Long id) {
        Optional<Movie> movieInDb =  movieRepository.findById(id);
        if (movieInDb.isEmpty()) {
            throw new EntityNotFoundException(String.format("Фильм с данным id '%s' не найден", id));
        }
        Movie movie = movieInDb.get();

        return movieMapper.toMovieCard(movie);
    }

    @Transactional
    public MoviePages getMoviesByName(String name, Boolean findOnKp, Integer page, Integer limit) {
        log.info("ФЛАГ findKpOn = " + findOnKp.toString());
        log.info("ЗАШЕЛ В СЕРВИС РАСШИРЕННОГО ПОИСКА");
        if (Boolean.TRUE.equals(findOnKp)) {
            MoviePages kpMovies = externalApiService.findMovieByName(name, page, limit);
            log.info("ИЩУ НА КП В МУВИ СЕРВИС РАСШИРЕННЫЙ ПОИСК");
            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();

            movieRepository.saveAll(movies);

            return kpMovies;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByTitleFilter(new TitleFilter(name), pageRequest);
        log.info("ИЩУ В БАЗЕ");
        return pageMapper.buildMoviePage(limit, page, moviePage);
    }

    @Transactional
    public MoviePagesShort getMoviesByNameShortInfo(String name, Boolean findOnKp, Integer page, Integer limit) {
        log.info("ФЛАГ findKpOn = " + findOnKp.toString());
        log.info("ЗАШЕЛ В СЕРВИС КРАТКОГО ПОИСКА");
        if (Boolean.TRUE.equals(findOnKp)) {
            MoviePages kpMovies = externalApiService.findMovieByName(name, page, limit);

            List<Movie> movies = kpMovies.getMovies().stream()
                    .filter(e -> movieRepository.findById(e.getId()).isEmpty())
                    .map(movieSerializer)
                    .toList();
            log.info("ИЩУ НА КП В МУВИ СЕРВИС КРАТКИЙ ПОИСК");
            movieRepository.saveAll(movies);
            return pageMapper.buildMoviePageShortFromKp(limit, page, kpMovies, movies);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Movie> moviePage = movieCustomRepository.findByTitleFilter(new TitleFilter(name), pageRequest);
        log.info("ИЩУ В БАЗЕ");
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


}