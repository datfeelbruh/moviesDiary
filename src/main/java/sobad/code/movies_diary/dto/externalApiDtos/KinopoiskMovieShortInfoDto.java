package sobad.code.movies_diary.dto.externalApiDtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class KinopoiskMovieShortInfoDto {
    private Integer id;
    private String name;
    private Integer year;
}
