package sobad.code.movies_diary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReviewDtoUpdateRequest {
    private String review;
    private Double rating;
}
