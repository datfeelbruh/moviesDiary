package sobad.code.moviesdiary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularMovieDto {
    private Long reviewCount;
    private Long id;
    private String title;
    private String poster;
}
