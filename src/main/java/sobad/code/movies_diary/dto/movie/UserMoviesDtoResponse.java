package sobad.code.movies_diary.dto.movie;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserMoviesDtoResponse {
    private String username;
    private List<UserMovieDto> movies;
}
