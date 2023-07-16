package sobad.code.movies_diary.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieReview {
    private String username;
    private Double rating;
    private String review;
}
