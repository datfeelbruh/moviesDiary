package sobad.code.moviesdiary.mappers.entity_serializers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.review.ReviewDto;
import sobad.code.moviesdiary.dtos.review.UserReview;
import sobad.code.moviesdiary.entities.Review;
import sobad.code.moviesdiary.services.UserService;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ReviewSerializer implements Function<Review, ReviewDto> {
    private final UserService userService;
    @Override
    public ReviewDto apply(Review review) {

        return ReviewDto.builder()
                .id(review.getId())
                .user(userService.getUserById(review.getUser().getId()))
                .movieId(review.getMovie().getId())
                .review(review.getUserReview())
                .rating(review.getRating())
                .build();
    }
}
