package sobad.code.movies_diary.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.MovieDtoRequest;
import sobad.code.movies_diary.dto.MovieDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.MovieRating;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.service.GenreService;
import sobad.code.movies_diary.service.MovieRatingService;
import sobad.code.movies_diary.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final GenreService genreService;

    public Movie mapFromRequestDto(MovieDtoRequest movieDtoRequest) {
        return Movie.builder()
                .movieName(movieDtoRequest.getMovieName())
                .kpId(movieDtoRequest.getKpId())
                .imdbRating(movieDtoRequest.getImdbRating())
                .kpRating(movieDtoRequest.getKpRating())
                .releaseYear(movieDtoRequest.getReleaseYear())
                .posterUrl(movieDtoRequest.getPosterUrl())
                .review(movieDtoRequest.getReview())
                .genres(genreService.getGenres(movieDtoRequest.getGenres()))
                .build();
    }

    public MovieDtoResponse mapFromEntityToResponseDto(Movie movie) {
        return MovieDtoResponse.builder()
                .id(movie.getId())
                .movieName(movie.getMovieName())
                .kpId(movie.getKpId())
                .imdbRating(movie.getImdbRating())
                .kpRating(movie.getKpRating())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPosterUrl())
                .review(movie.getReview())
                .genres(new HashSet<>(movie.getGenres()))
                .build();
    }
}
