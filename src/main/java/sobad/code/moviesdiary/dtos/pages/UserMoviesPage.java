package sobad.code.moviesdiary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.moviesdiary.dtos.movie.UserMovie;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMoviesPage extends PageDto {
    private UserDtoResponse user;
    private List<UserMovie> movies;
}
