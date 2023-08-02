package sobad.code.movies_diary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sobad.code.movies_diary.dtos.movie.UserMovie;
import sobad.code.movies_diary.dtos.user.UserDtoResponse;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMoviesPage {
    private UserDtoResponse user;
    private List<UserMovie> movies;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
