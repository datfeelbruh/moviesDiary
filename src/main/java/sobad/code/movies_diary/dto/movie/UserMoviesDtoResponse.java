package sobad.code.movies_diary.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMoviesDtoResponse {
    private String username;
    private List<UserMovieDto> movies;
}
