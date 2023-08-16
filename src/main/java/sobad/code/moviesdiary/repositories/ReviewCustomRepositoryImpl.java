package sobad.code.moviesdiary.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.movie.PopularMovieDto;
import sobad.code.moviesdiary.entities.Movie;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl {
    private final EntityManager entityManager;

    public List<PopularMovieDto> getPopularMovies() {
        return entityManager.createQuery("""
                        SELECT r.movie as movie, COUNT(r.movie) as count
                        FROM Review r
                        GROUP BY r.movie
                        """, Tuple.class)
                .setMaxResults(5)
                .getResultList()
                .stream()
                .map(tuple -> {
                    Movie movie = (Movie) tuple.get("movie");
                    return PopularMovieDto.builder()
                            .id(movie.getId())
                            .title(movie.getTitle())
                            .poster(movie.getPosterUrl())
                            .reviewCount((Long) tuple.get("count"))
                            .build();
                })
                .toList();
    }
}