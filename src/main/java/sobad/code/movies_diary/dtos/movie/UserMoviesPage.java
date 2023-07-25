package sobad.code.movies_diary.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMoviesPage {
    private Long userId;
    private String username;
    private List<UserMovie> movies;
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
