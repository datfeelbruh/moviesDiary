package sobad.code.movies_diary.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDtoResponse {
    private List<ReviewDto> reviews;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
