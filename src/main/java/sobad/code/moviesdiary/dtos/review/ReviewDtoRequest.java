package sobad.code.moviesdiary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReviewDtoRequest {
    private Long movieId;
    private String review;
    private Double rating;
}
