package sobad.code.moviesdiary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDtoShort {
    private String title;
    private Long id;
    private String posterUrl;
    private Integer releaseYear;
}
