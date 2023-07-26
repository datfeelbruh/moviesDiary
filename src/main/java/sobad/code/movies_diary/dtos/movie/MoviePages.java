package sobad.code.movies_diary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoviePages {
    private List<MovieDto> movies;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
