package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.ReviewDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public void create(String userReview, Double rating, Movie movie, User user) {
        Review review = Review.builder()
                .rating(rating)
                .review(userReview)
                .movie(movie)
                .user(user)
                .build();

        reviewRepository.save(review);
    }

    public List<ReviewDto> getReviewByKpId(Long id) {
        return reviewRepository.findAllByMovieId(id)
                .stream()
                .map(e -> ReviewDto.builder()
                        .review(e.getReview())
                        .rating(e.getRating())
                        .username(e.getUser().getUsername())
                        .build())
                .toList();
    }

    public ReviewDto getReviewByUserIdAndMovieID(Long userId, Long movieId) {
        Review review = reviewRepository.findAllByUserIdAndMovieId(userId, movieId);
        return ReviewDto.builder()
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .review(review.getReview())
                .build();
    }
}
