package sobad.code.movies_diary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.GenresItem;
import java.util.List;

@Getter
@Setter
@Builder
public class MovieDtoRequest {
    private Long kpId;
    private String movieName;
    private Integer releaseYear;
    private String review;
    private Double averageRating;
    private Double userRating;
    private Double kpRating;
    private Double imdbRating;
    private String posterUrl;
    private List<GenresItem> genres;
}
