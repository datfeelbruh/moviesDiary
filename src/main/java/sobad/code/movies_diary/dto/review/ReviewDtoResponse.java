package sobad.code.movies_diary.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDtoResponse {
    private Long id;
    private String username;
    private Long movieId;
    private UserReview userReview;
}
