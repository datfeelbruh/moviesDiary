package sobad.code.moviesdiary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.GenreDto;
import sobad.code.moviesdiary.dtos.movie.MovieCard;
import sobad.code.moviesdiary.dtos.movie.UserMovie;
import sobad.code.moviesdiary.dtos.review.ReviewDto;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.services.ReviewService;
import sobad.code.moviesdiary.services.UserService;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final ReviewService reviewService;
    private final UserService userService;

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
                .review(reviewByUser.getReview())
                .rating(reviewByUser.getRating())
                .build();
    }

    public MovieCard toMovieCard(Movie movie, Long userId) {
        return MovieCard.builder()
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
                .isFavorite(userService.isFavoriteMovieForCurrentUser(userId, movie.getId()))
                .build();
    }
}
