package sobad.code.movies_diary.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserReview {
    private String review;
    private Double rating;
}
