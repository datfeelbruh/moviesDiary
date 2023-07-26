package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MovieDtoShort;
import sobad.code.movies_diary.dtos.movie.MoviePages;
import sobad.code.movies_diary.dtos.movie.MoviePagesShort;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.mappers.entitySerializers.MovieDtoSerializer;
import sobad.code.movies_diary.mappers.entitySerializers.MovieDtoShortSerializer;

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

        return MoviePages.builder()
                .movies(movies)
                .page(page)
                .pages(moviePage.getTotalPages())
                .total(moviePage.getTotalElements())
                .limit(limit)
                .build();
    }

    public MoviePagesShort buildMoviePageShortFromKp(Integer limit, Integer page,
                                                     MoviePages moviePage, List<Movie> movies) {
        List<MovieDtoShort> movieDtoShorts = movies.stream()
                .map(movieDtoShortSerializer)
                .toList();

        return MoviePagesShort.builder()
                .movies(movieDtoShorts)
                .page(page)
                .pages(moviePage.getPages())
                .total(moviePage.getTotal())
                .limit(limit)
                .build();
    }

    public MoviePagesShort buildMoviePageShort(Integer limit, Integer page, Page<Movie> moviePage) {
        List<MovieDtoShort> movies = moviePage.getContent().stream()
                .map(movieDtoShortSerializer)
                .toList();

        return MoviePagesShort.builder()
                .movies(movies)
                .page(page)
                .pages(moviePage.getTotalPages())
                .total(moviePage.getTotalElements())
                .limit(limit)
                .build();
    }
}
