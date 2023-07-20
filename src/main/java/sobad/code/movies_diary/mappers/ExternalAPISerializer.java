package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.MovieReview;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.service.GenreService;
import sobad.code.movies_diary.service.ReviewService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExternalAPISerializer implements Function<DocsItemMovieInfo, MovieCard> {
    private final ReviewService reviewService;
    private final GenreService genreService;

    @Override
    public MovieCard apply(DocsItemMovieInfo movieInfo) {
        return MovieCard.builder()
                .id(movieInfo.getId())
                .description(movieInfo.getDescription())
                .title(movieInfo.getName())
                .releaseYear(movieInfo.getYear())
                .kpRating(movieInfo.getRating().getKp())
                .imdbRating(movieInfo.getRating().getImdb())
                .averageRating(reviewService.getAverageReviewRatingById(movieInfo.getId()))
                .genres(
                        movieInfo.getGenres().stream()
                                .map(e -> genreService.findGenre(e.getName()))
                                .map(e -> new GenreDto(e.getName()))
                                .collect(Collectors.toSet())
                )
                .posterUrl(movieInfo.getPoster().getUrl())
                .reviews(reviewService.getReviewByMovieId(movieInfo.getId()).stream()
                        .map(e -> new MovieReview(
                                e.getUsername(),
                                e.getUserReview().getRating(),
                                e.getUserReview().getReview())
                        ).toList()
                )
                .build();
    }
}
