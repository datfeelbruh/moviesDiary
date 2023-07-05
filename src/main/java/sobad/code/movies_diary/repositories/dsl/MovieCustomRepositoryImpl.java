package sobad.code.movies_diary.repositories.dsl;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.repositories.dsl.filters.MovieFilter;

import static sobad.code.movies_diary.entities.QMovie.movie;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {
    private final EntityManager entityManager;
    @Override
    public List<Movie> findByFilter(MovieFilter filter) {
        return new JPAQuery<Movie>(entityManager)
                .select(movie)
                .from(movie)
                .where(movie.genres.any().name.contains(filter.getGenreName()))
                .fetch();
    }

}
