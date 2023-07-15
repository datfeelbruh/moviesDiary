package sobad.code.movies_diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDtoResponse {
    private Long id;
    private String movieName;
    private String description;
    private Integer releaseYear;
    private Double kpRating;
    private Double imdbRating;
    private Set<GenreDto> genres;
    private String posterUrl;
    private List<ReviewDto> reviews;
}
