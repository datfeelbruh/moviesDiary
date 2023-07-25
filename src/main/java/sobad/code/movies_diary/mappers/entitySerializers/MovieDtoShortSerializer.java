package sobad.code.movies_diary.mappers.entitySerializers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.movie.MovieDtoShort;
import sobad.code.movies_diary.entities.Movie;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MovieDtoShortSerializer implements Function<Movie, MovieDtoShort> {
    @Override
    public MovieDtoShort apply(Movie movie) {
        return MovieDtoShort.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
