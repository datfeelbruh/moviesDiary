package sobad.code.movies_diary.mappers;

import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.dto.review.UserReview;
import sobad.code.movies_diary.entities.Review;

import java.util.function.Function;

@Component
public class ReviewSerializer implements Function<Review, ReviewDtoResponse> {
    @Override
    public ReviewDtoResponse apply(Review review) {

        return ReviewDtoResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .movieId(review.getMovie().getId())
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }
}
