package sobad.code.moviesdiary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.moviesdiary.dtos.movie.MovieCard;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesMoviesPage extends PageDto {
    List<MovieCard> movies;
}
