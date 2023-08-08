package sobad.code.moviesdiary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieReview {
    private Long id;
    private UserDtoResponse user;
    private Double rating;
    private String review;
}
