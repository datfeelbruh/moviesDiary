package sobad.code.moviesdiary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.moviesdiary.dtos.review.ReviewDto;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewPages extends PageDto {
    private List<ReviewDto> reviews;
}
