package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.MovieRating;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.MovieRatingRepository;

import java.util.OptionalDouble;


@Service
@RequiredArgsConstructor
public class MovieRatingService {
    private final MovieRatingRepository movieRatingRepository;

    public MovieRating create(Movie movie, User user, Double rating) {
        MovieRating movieRating = MovieRating.builder()
                .movie(movie)
                .user(user)
                .rating(rating)
                .build();

        return movieRatingRepository.save(movieRating);
    }

    public Double getRatingById(Long movieId, Long userId) {
        return movieRatingRepository.findByMovieIdAndUserId(movieId, userId).get().getRating();
    }

    public OptionalDouble calcAverageRating(Long movieId) {
        return movieRatingRepository.findAllByMovieId(movieId)
                .stream()
                .mapToDouble(MovieRating::getRating)
                .average();
    }
}
