package sobad.code.moviesdiary.dtos.external_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info.GenresItem;

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
