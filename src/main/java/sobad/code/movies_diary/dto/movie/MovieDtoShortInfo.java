package sobad.code.movies_diary.dto.movie;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieDtoShortInfo {
    private String title;
    private Long id;
    private String posterUrl;
    private Integer releaseYear;
}
