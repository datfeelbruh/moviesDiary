package sobad.code.movies_diary.mappers.entitySerializers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dtos.review.ReviewDto;
import sobad.code.movies_diary.dtos.review.UserReview;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.services.UserService;

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
                .userReview(new UserReview(review.getReview(), review.getRating()))
                .build();
    }
}
