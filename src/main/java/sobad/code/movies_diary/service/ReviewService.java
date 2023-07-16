package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.review.UserReview;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;
import sobad.code.movies_diary.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final Integer countReviewToCalcAverage = 5;

    public ReviewDtoResponse createReview(ReviewDtoRequest reviewDtoRequest) {
        User user = userService.getCurrentUser();
        Movie movie = movieRepository.findById(reviewDtoRequest.getMovieId()).orElseThrow();

        Review review = Review.builder()
                .rating(reviewDtoRequest.getRating())
                .review(reviewDtoRequest.getReview())
                .movie(movie)
                .user(user)
                .build();

        reviewRepository.save(review);

        return ReviewDtoResponse.builder()
                .id(review.getId())
                .username(user.getUsername())
                .movieId(movie.getId())
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }


    public ReviewDtoResponse getReviewByUserIdAndMovieID(Long userId, Long movieId) {
        User user = userRepository.findById(userId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        Review review = reviewRepository.findAllByUserIdAndMovieId(userId, movieId);

        return ReviewDtoResponse.builder()
                .username(user.getUsername())
                .movieId(movie.getId())
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }

    public List<ReviewDtoResponse> getReviewByMovieId(Long movieId) {
        return reviewRepository.findAllByMovieId(movieId)
                .stream()
                .map(e -> ReviewDtoResponse.builder()
                        .id(e.getId())
                        .username(e.getUser().getUsername())
                        .movieId(e.getMovie().getId())
                        .userReview(new UserReview(e.getReview(), e.getRating()))
                        .build())
                .toList();
    }

    public List<ReviewDtoResponse> getReviewByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId)
                .stream()
                .map(e -> ReviewDtoResponse.builder()
                        .id(e.getId())
                        .username(e.getUser().getUsername())
                        .movieId(e.getMovie().getId())
                        .userReview(new UserReview(e.getReview(), e.getRating()))
                        .build())
                .toList();
    }

    public Double getAverageReviewRatingById(Long id) {
        Integer count = reviewRepository.countByMovieId(id);
        if (count > 0 && count % countReviewToCalcAverage == 0) {
            return getAverage(id);
        }
        return null;
    }

    public ReviewDtoResponse updateReview(Long reviewId, ReviewDtoRequest reviewDtoRequest) {
        User user = userService.getCurrentUser();
        Movie movie = movieRepository.findById(reviewDtoRequest.getMovieId()).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        review.setId(reviewId);
        review.setReview(reviewDtoRequest.getReview());
        review.setRating(reviewDtoRequest.getRating());

        reviewRepository.save(review);

        return ReviewDtoResponse.builder()
                .id(reviewId)
                .username(user.getUsername())
                .movieId(movie.getId())
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Double getAverage(Long id) {
        Double avg = reviewRepository.getAvgRatingByMovieId(id);
        return Math.round(avg * 10.0) / 10.0;
    }
}
