package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.dto.UserMovieDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.service.GenreService;
import sobad.code.movies_diary.service.ReviewService;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final ReviewService reviewService;
    private final GenreService genreService;
    public MovieDtoResponse mapFromEntityToResponse(Movie movie) {
        return MovieDtoResponse.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .movieName(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .genres(movie.getGenres().stream()
                        .map(e -> new GenreDto(e.getName()))
                        .collect(Collectors.toSet()))
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .reviews(reviewService.getReviewByKpId(movie.getId()))
                .build();
    }

    public UserMovieDto mapFromEntityToUserMovieResponse(Movie movie, User user) {
        return UserMovieDto.builder()
                .username(user.getUsername())
                .id(movie.getId())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .movieName(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .genres(movie.getGenres().stream()
                        .map(e -> new GenreDto(e.getName()))
                        .collect(Collectors.toSet()))
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .review(reviewService.getReviewByUserIdAndMovieID(user.getId(), movie.getId()))
                .build();
    }

    public MovieDtoResponse mapFromKinopoiskToMovieInfo(DocsItemMovieInfo movie) {
        return MovieDtoResponse.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .movieName(movie.getName())
                .releaseYear(movie.getYear())
                .kpRating(movie.getRating().getKp())
                .imdbRating(movie.getRating().getImdb())
                .genres(
                        movie.getGenres().stream()
                                .map(e -> genreService.findGenre(e.getName()))
                                .map(e -> new GenreDto(e.getName()))
                                .collect(Collectors.toSet())
                )
                .posterUrl(movie.getPoster().getUrl())
                .reviews(reviewService.getReviewByKpId(movie.getId()))
                .build();
    }

    public Movie mapFromMovieInfoToEntity(MovieDtoResponse movie) {
        return Movie.builder()
                .id(movie.getId())
                .description(movie.getDescription())
                .movieName(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .posterUrl(movie.getPosterUrl())
                .genres(movie.getGenres().stream()
                        .map(genre -> genreService.findGenre(genre.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
