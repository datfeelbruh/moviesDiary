package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.service.GenreService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieCardSerializer implements Function<MovieCard, Movie> {
    private final GenreService genreService;

    @Override
    public Movie apply(MovieCard movieCardResponse) {
        return Movie.builder()
                .id(movieCardResponse.getId())
                .description(movieCardResponse.getDescription())
                .movieName(movieCardResponse.getTitle())
                .releaseYear(movieCardResponse.getReleaseYear())
                .imdbRating(movieCardResponse.getImdbRating())
                .kpRating(movieCardResponse.getKpRating())
                .posterUrl(movieCardResponse.getPosterUrl())
                .genres(movieCardResponse.getGenres().stream()
                        .map(genre -> genreService.findGenre(genre.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
