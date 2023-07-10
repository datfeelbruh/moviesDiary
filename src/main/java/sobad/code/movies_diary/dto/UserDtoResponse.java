package sobad.code.movies_diary.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDtoResponse {
    private String username;
    private Set<MovieDtoResponse> movies;
}
