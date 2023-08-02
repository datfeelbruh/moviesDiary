package sobad.code.movies_diary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.movies_diary.dtos.review.ReviewDto;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewPages {
    private List<ReviewDto> reviews;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
