package sobad.code.movies_diary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDto {
    private Long id;
    private String username;
    private Long userId;
    private Long movieId;
    private UserReview userReview;
}
