package sobad.code.moviesdiary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.moviesdiary.dtos.movie.MovieDtoShort;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoviePagesShort extends PageDto {
    private List<MovieDtoShort> movies;
}
