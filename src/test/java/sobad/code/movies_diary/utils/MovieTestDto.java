package sobad.code.movies_diary.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.movies_diary.dto.Dto;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.dto.movie.MovieReview;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieTestDto implements Dto {
    private Long id;
    private String title;
    private String description;
    private Integer releaseYear;
    private Double kpRating;
    private Double imdbRating;
    private Set<GenreDto> genres;
    private String posterUrl;
}
