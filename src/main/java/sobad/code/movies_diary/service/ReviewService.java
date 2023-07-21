package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.review.UserReview;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.MovieNotFoundException;
import sobad.code.movies_diary.exceptions.ReviewNotFoundException;
import sobad.code.movies_diary.mappers.ReviewSerializer;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;
import sobad.code.movies_diary.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final ReviewSerializer reviewSerializer;

    public ReviewDtoResponse createReview(ReviewDtoRequest reviewDtoRequest) {
        User user = userService.getCurrentUser();
        if (reviewRepository.findAllByUserIdAndMovieId(user.getId(), reviewDtoRequest.getMovieId()).isPresent()) {
            throw new ReviewNotFoundException("Ревью на этот фильм уже создано");
        }
        Movie movie = movieRepository.findById(reviewDtoRequest.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(
                        String.format("Фильм с данным id '%s' не найден", reviewDtoRequest.getMovieId()))
                );

        Review review = Review.builder()
                .rating(reviewDtoRequest.getRating())
                .review(reviewDtoRequest.getReview())
                .movie(movie)
                .user(user)
                .build();

        reviewRepository.save(review);


        return reviewSerializer.apply(review);
    }

    public List<ReviewDtoResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewSerializer)
                .toList();
    }


    public ReviewDtoResponse getReviewByUserIdAndMovieId(Long userId, Long movieId) {
        Review review = reviewRepository.findAllByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        String.format(
                                "Ревью на фильм с id '%s' от пользователя с айди '%s' не найдено", userId, movieId)
                ));

        return reviewSerializer.apply(review);
    }

    public List<ReviewDtoResponse> getReviewByMovieId(Long movieId) {
        return reviewRepository.findAllByMovieId(movieId)
                .stream()
                .map(reviewSerializer)
                .toList();
    }

    public List<ReviewDtoResponse> getReviewByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId)
                .stream()
                .map(reviewSerializer)
                .toList();
    }

    public Double getAverageReviewRatingById(Long id) {
        Integer count = reviewRepository.countByMovieId(id);
        Integer countReviewToCalcAverage = 5;
        if (count > 0 && count % countReviewToCalcAverage == 0) {
            return getAverage(id);
        }
        return null;
    }

    public ReviewDtoResponse updateReview(Long reviewId, ReviewDtoRequest reviewDtoRequest) {
        movieRepository.findById(reviewDtoRequest.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(
                        String.format("Фильм с данным id '%s' не найден",  reviewDtoRequest.getMovieId()))
                );
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        String.format("Ревью с данным id '%s' не найдено", reviewId))
                );

        review.setId(reviewId);
        review.setReview(reviewDtoRequest.getReview());
        review.setRating(reviewDtoRequest.getRating());

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Double getAverage(Long id) {
        Double avg = reviewRepository.getAvgRatingByMovieId(id);
        return Math.round(avg * 10.0) / 10.0;
    }
}
