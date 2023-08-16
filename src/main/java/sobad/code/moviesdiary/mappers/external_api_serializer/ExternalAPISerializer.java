package sobad.code.moviesdiary.mappers.external_api_serializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.GenreDto;
import sobad.code.moviesdiary.dtos.movie.MovieDto;
import sobad.code.moviesdiary.dtos.movie.MovieReview;
import sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info.DocsItemMovieInfo;
import sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info.Rating;
import sobad.code.moviesdiary.services.GenreService;
import sobad.code.moviesdiary.services.ReviewService;

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
                                e.getRating(),
                                e.getReview())
                        ).toList()
                )
                .build();
    }
}
