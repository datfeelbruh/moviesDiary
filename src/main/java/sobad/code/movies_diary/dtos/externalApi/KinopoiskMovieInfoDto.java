package sobad.code.movies_diary.dtos.externalApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.GenresItem;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KinopoiskMovieInfoDto {
    private Long kpId;
    private String movieName;
    private Integer releaseYear;
    private Double kpRating;
    private Double imdbRating;
    private String posterUrl;
    private String description;
    private List<GenresItem> genres;
}