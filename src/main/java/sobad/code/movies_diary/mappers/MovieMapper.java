package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.GenreDto;
import sobad.code.movies_diary.dtos.movie.UserMovie;
import sobad.code.movies_diary.dtos.review.ReviewDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.services.ReviewService;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final ReviewService reviewService;

    public UserMovie toUserMovieDto(Movie movie, Long userId) {
        ReviewDto reviewByUser = reviewService.getReviewByUserIdAndMovieId(userId, movie.getId());
        return UserMovie.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .title(movie.getTitle())
                .averageRating(reviewService.getAverageReviewRatingById(movie.getId()))
                .releaseYear(movie.getReleaseYear())
                .genres(movie.getGenres().stream()
                        .map(e -> new GenreDto(e.getName()))
                        .collect(Collectors.toSet()))
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .review(reviewByUser.getUserReview().getReview())
                .rating(reviewByUser.getUserReview().getRating())
                .build();
    }
}
