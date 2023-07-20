package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.movie.MovieShortInfo;
import sobad.code.movies_diary.entities.Movie;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MovieEntityShortSerializer implements Function<Movie, MovieShortInfo> {
    @Override
    public MovieShortInfo apply(Movie movie) {
        return MovieShortInfo.builder()
                .id(movie.getId())
                .title(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
