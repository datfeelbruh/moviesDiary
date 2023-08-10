package sobad.code.movies_diary.mappers.externalApiSerializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.GenreDto;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MovieReview;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.Rating;
import sobad.code.movies_diary.services.GenreService;
import sobad.code.movies_diary.services.ReviewService;
import sobad.code.movies_diary.services.UserService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExternalAPISerializer implements Function<DocsItemMovieInfo, MovieDto> {
    private final ReviewService reviewService;
    private final GenreService genreService;

    @Override
    public MovieDto apply(DocsItemMovieInfo movieInfo) {
        return MovieDto.builder()
                .id(movieInfo.getId())
                .description(movieInfo.getDescription().orElse(""))
                .title(movieInfo.getName())
                .releaseYear(movieInfo.getYear())
                .kpRating(movieInfo.getRating().orElse(new Rating(0.0, 0.0)).getKp())
                .imdbRating(movieInfo.getRating().orElse(new Rating(0.0, 0.0)).getImdb())
                .averageRating(reviewService.getAverageReviewRatingById(movieInfo.getId()))
                .genres(
                        movieInfo.getGenres().stream()
                                .map(e -> genreService.findGenre(e.getName()))
                                .map(e -> new GenreDto(e.getName()))
                                .collect(Collectors.toSet())
                )
                .posterUrl(movieInfo.getPoster() == null ? "" : movieInfo.getPoster().getUrl())
                .reviews(reviewService.getRandomReviewsByMovieId(movieInfo.getId()).stream()
                        .map(e -> new MovieReview(
                                e.getId(),
                                e.getUser(),
                                e.getUserReview().getRating(),
                                e.getUserReview().getReview())
                        ).toList()
                )
                .build();
    }
}
