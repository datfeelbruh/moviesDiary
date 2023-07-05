package sobad.code.movies_diary.dto.externalApiDtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.GenresItem;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class KinopoiskMovieInfoDto {
    private Long kpId;
    private String movieName;
    private Integer releaseYear;
//    private String review;
    private Double kpRating;
    private Double imdbRating;
//    private Double userRating;
//    private Double otherUsersRating;
    private String posterUrl;
    private Set<GenresItem> genres;
}
