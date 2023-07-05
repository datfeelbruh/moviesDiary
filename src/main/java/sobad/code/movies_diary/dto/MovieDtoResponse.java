package sobad.code.movies_diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sobad.code.movies_diary.entities.Genre;
import java.util.Set;

@Getter
@Setter
@Builder
public class MovieDtoResponse {
    private Long id;
    private Long kpId;
    private String movieName;
    private Integer releaseYear;
    private String review;
    private Double kpRating;
    private Double imdbRating;
    private Double userRating;
    private Double averageRating;
    private String posterUrl;
    private Set<Genre> genres;
}
