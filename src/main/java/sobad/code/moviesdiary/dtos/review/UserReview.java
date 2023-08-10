package sobad.code.moviesdiary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserReview {
    private String review;
    private Double rating;
}
