package sobad.code.movies_diary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieShortInfo {
    private String title;
    private Long id;
    private String posterUrl;
    private Integer releaseYear;
}
