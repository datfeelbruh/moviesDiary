package sobad.code.movies_diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.MovieRating;

import java.util.Set;

@Data
@Builder
public class UserDtoResponse {
    private String username;
    private Set<MovieDtoResponse> movies;
}
