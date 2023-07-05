package sobad.code.movies_diary.repositories.dsl;

import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.repositories.dsl.filters.MovieFilter;

import java.util.List;

public interface MovieCustomRepository {
    List<Movie> findByFilter(MovieFilter filter);
}
