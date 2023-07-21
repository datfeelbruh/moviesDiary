package sobad.code.movies_diary.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieReview {
    private Long id;
    private Long userId;
    private String username;
    private Double rating;
    private String review;
}
