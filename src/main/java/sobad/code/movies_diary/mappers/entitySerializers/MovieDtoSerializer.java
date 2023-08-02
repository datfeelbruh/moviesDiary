package sobad.code.movies_diary.mappers.entitySerializers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.GenreDto;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MovieReview;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.services.ReviewService;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieDtoSerializer implements Function<Movie, MovieDto> {

    private final ReviewService reviewService;

    @Override
    public MovieDto apply(Movie movie) {
        return MovieDto.builder()
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
                .reviews(
                        reviewService.getRandomReviewsByMovieId(movie.getId()).stream()
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
