package sobad.code.movies_diary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.dtos.movie.MovieCard;
import sobad.code.movies_diary.dtos.pages.MoviePages;
import sobad.code.movies_diary.dtos.pages.MoviePagesShort;
import sobad.code.movies_diary.dtos.movie.UserMovie;
import sobad.code.movies_diary.dtos.pages.UserMoviesPage;
import sobad.code.movies_diary.entities.Genre;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.entiryExceptions.EntityNotFoundException;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.mappers.PageMapper;
import sobad.code.movies_diary.mappers.entitySerializers.MovieDtoSerializer;
import sobad.code.movies_diary.mappers.entitySerializers.MovieSerializer;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.dsl.MovieCustomRepositoryImpl;
import sobad.code.movies_diary.repositories.dsl.filters.GenreFilter;
import sobad.code.movies_diary.repositories.dsl.filters.TitleFilter;
import sobad.code.movies_diary.repositories.dsl.filters.UserIdFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieCustomRepositoryImpl movieCustomRepository;
    private final MovieSerializer movieSerializer;
    private final MovieDtoSerializer movieDtoSerializer;
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
        if (findOnKp) {
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
        if (findOnKp) {
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

        return UserMoviesPage.builder()
                .user(userService.getUserById(userId))
                .movies(userMovies)
                .total(movies.getTotalElements())
                .page(page)
                .limit(limit)
                .pages(movies.getTotalPages())
                .build();
    }

    public List<Map<String, String>> getMoviesName() {
        Map<Long, String> movies = movieCustomRepository.getTitlesWithId();
        List<Map<String, String>> response = new ArrayList<>();
        movies.forEach((k, v) -> {
            Map<String, String> movie = new HashMap<>();
            movie.put("id", k.toString());
            movie.put("title", v);
            response.add(movie);
        });
        return response;
    }


}
