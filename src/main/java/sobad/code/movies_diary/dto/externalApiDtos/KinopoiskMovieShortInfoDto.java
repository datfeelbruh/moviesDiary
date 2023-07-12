package sobad.code.movies_diary.dto.externalApiDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KinopoiskMovieShortInfoDto {
    private Integer id;
    private String name;
    private Integer year;
}
