package sobad.code.movies_diary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.movies_diary.dtos.user.UserDtoResponse;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDto {
    private Long id;
    private UserDtoResponse user;
    private Long movieId;
    private UserReview userReview;
}
