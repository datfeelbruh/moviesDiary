package sobad.code.movies_diary.dto.review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDtoResponse {
    private Long id;
    private String username;
    private Long movieId;
    private UserReview userReview;
}
