package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.MovieShortInfo;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExternalAPIShortInfoSerializer implements Function<MovieCard, MovieShortInfo> {

    @Override
    public MovieShortInfo apply(MovieCard movieCardResponse) {
        return MovieShortInfo.builder()
                .id(movieCardResponse.getId())
                .title(movieCardResponse.getTitle())
                .posterUrl(movieCardResponse.getPosterUrl())
                .releaseYear(movieCardResponse.getReleaseYear())
                .build();
    }
}
