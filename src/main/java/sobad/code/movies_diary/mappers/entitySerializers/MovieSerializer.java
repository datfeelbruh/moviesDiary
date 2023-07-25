package sobad.code.movies_diary.mappers.entitySerializers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.services.GenreService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieSerializer implements Function<MovieDto, Movie> {
    private final GenreService genreService;

    @Override
    public Movie apply(MovieDto movieDto) {
        return Movie.builder()
                .id(movieDto.getId())
                .description(movieDto.getDescription())
                .title(movieDto.getTitle())
                .releaseYear(movieDto.getReleaseYear())
                .imdbRating(movieDto.getImdbRating())
                .kpRating(movieDto.getKpRating())
                .posterUrl(movieDto.getPosterUrl())
                .genres(movieDto.getGenres().stream()
                        .map(genre -> genreService.findGenre(genre.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
