package sobad.code.movies_diary.mappers.entitySerializers;

import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.review.ReviewDto;
import sobad.code.movies_diary.dtos.review.UserReview;
import sobad.code.movies_diary.entities.Review;

import java.util.function.Function;

@Component
public class ReviewSerializer implements Function<Review, ReviewDto> {
    @Override
    public ReviewDto apply(Review review) {

        return ReviewDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .movieId(review.getMovie().getId())
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }
}
