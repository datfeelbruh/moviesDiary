package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieCard;
import sobad.code.movies_diary.dto.movie.MovieShortInfo;
import sobad.code.movies_diary.dto.movie.MovieReview;
import sobad.code.movies_diary.dto.movie.UserMovie;
import sobad.code.movies_diary.dto.movie.UserMovies;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.service.GenreService;
import sobad.code.movies_diary.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final ReviewService reviewService;

    public UserMovies mapFromEntityToUserMovieResponse(List<Movie> movies, User user) {
        return UserMovies.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .movies(
                        movies.stream()
                                .map(e -> toUserMovieDto(e, user.getId()))
                                .toList()
                )
                .build();
    }

    public UserMovie toUserMovieDto(Movie movie, Long userId) {
        ReviewDtoResponse reviewByUser = reviewService.getReviewByUserIdAndMovieId(userId, movie.getId());
        return UserMovie.builder()
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
                .review(reviewByUser.getUserReview().getReview())
                .rating(reviewByUser.getUserReview().getRating())
                .build();
    }
}
