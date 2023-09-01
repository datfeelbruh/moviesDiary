package sobad.code.moviesdiary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.moviesdiary.dtos.GenreDto;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieCard {
    private Long id;
    private String title;
    private String description;
    private Integer releaseYear;
    private Double kpRating;
    private Double imdbRating;
    private Double averageRating;
    private Set<GenreDto> genres;
    private String posterUrl;
    private Boolean isFavorite;
}
