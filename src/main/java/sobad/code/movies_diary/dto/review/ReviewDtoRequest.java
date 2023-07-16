package sobad.code.movies_diary.dto.review;

import lombok.Data;

@Data
public class ReviewDtoRequest {
    private Long movieId;
    private String review;
    private Double rating;
}
