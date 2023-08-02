package sobad.code.movies_diary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.movies_diary.dtos.movie.MovieDtoShort;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoviePagesShort {
    private List<MovieDtoShort> movies;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
