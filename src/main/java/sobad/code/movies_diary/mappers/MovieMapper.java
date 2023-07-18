package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.dto.movie.MovieDtoShortInfo;
import sobad.code.movies_diary.dto.movie.MovieReview;
import sobad.code.movies_diary.dto.movie.UserMovieDto;
import sobad.code.movies_diary.dto.movie.UserMoviesDtoResponse;
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
    private final GenreService genreService;

    public MovieDtoShortInfo mapToShortInfo(Movie movie) {
        return MovieDtoShortInfo.builder()
                .id(movie.getId())
                .title(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
    public MovieDtoResponse mapFromEntityToResponse(Movie movie) {
        return MovieDtoResponse.builder()
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
                                        e.getUsername(),
                                        e.getUserReview().getRating(),
                                        e.getUserReview().getReview())
                                ).toList()
                )
                .build();
    }

    public UserMoviesDtoResponse mapFromEntityToUserMovieResponse(List<Movie> movies, User user) {
        return UserMoviesDtoResponse.builder()
                .username(user.getUsername())
                .movies(
                        movies.stream()
                                .map(e -> toUserMovieDto(e, user.getId()))
                                .toList()
                )
                .build();
    }

    public UserMovieDto toUserMovieDto(Movie movie, Long userId) {
        ReviewDtoResponse reviewByUser = reviewService.getReviewByUserIdAndMovieId(userId, movie.getId());
        return UserMovieDto.builder()
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

    public MovieDtoResponse mapFromKinopoiskToMovieInfo(DocsItemMovieInfo movie) {
        return MovieDtoResponse.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .title(movie.getName())
                .releaseYear(movie.getYear())
                .kpRating(movie.getRating().getKp())
                .imdbRating(movie.getRating().getImdb())
                .averageRating(reviewService.getAverageReviewRatingById(movie.getId()))
                .genres(
                        movie.getGenres().stream()
                                .map(e -> genreService.findGenre(e.getName()))
                                .map(e -> new GenreDto(e.getName()))
                                .collect(Collectors.toSet())
                )
                .posterUrl(movie.getPoster().getUrl())
                .reviews( reviewService.getReviewByMovieId(movie.getId()).stream()
                        .map(e -> new MovieReview(
                                e.getUsername(),
                                e.getUserReview().getRating(),
                                e.getUserReview().getReview())
                        ).toList()
                )
                .build();
    }

    public Movie mapFromMovieInfoToEntity(MovieDtoResponse movieDtoResponse) {
        return Movie.builder()
                .id(movieDtoResponse.getId())
                .description(movieDtoResponse.getDescription())
                .movieName(movieDtoResponse.getTitle())
                .releaseYear(movieDtoResponse.getReleaseYear())
                .imdbRating(movieDtoResponse.getImdbRating())
                .kpRating(movieDtoResponse.getKpRating())
                .posterUrl(movieDtoResponse.getPosterUrl())
                .genres(movieDtoResponse.getGenres().stream()
                        .map(genre -> genreService.findGenre(genre.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
