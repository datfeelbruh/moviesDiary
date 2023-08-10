package sobad.code.moviesdiary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.movie.MovieDto;
import sobad.code.moviesdiary.dtos.movie.MovieDtoShort;
import sobad.code.moviesdiary.dtos.pages.MoviePages;
import sobad.code.moviesdiary.dtos.pages.MoviePagesShort;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.mappers.entity_serializers.MovieDtoSerializer;
import sobad.code.moviesdiary.mappers.entity_serializers.MovieDtoShortSerializer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PageMapper {
    private final MovieDtoSerializer movieDtoSerializer;
    private final MovieDtoShortSerializer movieDtoShortSerializer;

    public MoviePages buildMoviePage(Integer limit, Integer page, Page<Movie> moviePage) {
        List<MovieDto> movies = moviePage.getContent().stream()
                .map(movieDtoSerializer)
                .toList();
        MoviePages moviePages = new MoviePages(movies);
        moviePages.setPage(page);
        moviePages.setPages(moviePage.getTotalPages());
        moviePages.setTotal(moviePage.getTotalElements());
        moviePages.setLimit(limit);

        return moviePages;
    }

    public MoviePagesShort buildMoviePageShortFromKp(Integer limit, Integer page,
                                                     MoviePages moviePage, List<Movie> movies) {
        List<MovieDtoShort> movieDtoShorts = movies.stream()
                .map(movieDtoShortSerializer)
                .toList();

        MoviePagesShort moviePagesShort = new MoviePagesShort(movieDtoShorts);
        moviePagesShort.setPage(page);
        moviePagesShort.setPages(moviePage.getPages());
        moviePagesShort.setTotal(moviePage.getTotal());
        moviePagesShort.setLimit(limit);

        return moviePagesShort;
    }

    public MoviePagesShort buildMoviePageShort(Integer limit, Integer page, Page<Movie> moviePage) {
        List<MovieDtoShort> movies = moviePage.getContent().stream()
                .map(movieDtoShortSerializer)
                .toList();

        MoviePagesShort moviePagesShort = new MoviePagesShort(movies);
        moviePagesShort.setPage(page);
        moviePagesShort.setPages(moviePage.getTotalPages());
        moviePagesShort.setTotal(moviePage.getTotalElements());
        moviePagesShort.setLimit(limit);

        return moviePagesShort;
    }
}
