package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.MovieReview;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.service.ReviewService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieEntitySerializer implements Function<Movie, MovieCard> {
    private final ReviewService reviewService;

    @Override
    public MovieCard apply(Movie movie) {
        return MovieCard.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .title(movie.getMovieName())
                .averageRating(reviewService.getAverageReviewRatingById(movie.getId()))
                .releaseYear(movie.getReleaseYear())
                .genres(movie.getGenres().stream()
                        .map(e -> new GenreDto(e.getName()))
                        .collect(Collectors.toSet()))
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .reviews(
                        reviewService.getReviewByMovieId(movie.getId()).stream()
                                .map(e -> new MovieReview(
                                        e.getId(),
                                        e.getUserId(),
                                        e.getUsername(),
                                        e.getUserReview().getRating(),
                                        e.getUserReview().getReview())
                                ).toList()
                )
                .build();
    }
}
