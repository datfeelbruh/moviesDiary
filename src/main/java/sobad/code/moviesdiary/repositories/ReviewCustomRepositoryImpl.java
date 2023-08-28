package sobad.code.moviesdiary.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.dtos.movie.PopularMovieDto;
import sobad.code.moviesdiary.entities.Movie;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl {
    private final EntityManager entityManager;

    public List<PopularMovieDto> getPopularMovies(Integer count) {
        return entityManager.createQuery("""
                        SELECT r.movie as movie, COUNT(r.movie) as count
                        FROM Review r
                        GROUP BY r.movie
                        """, Tuple.class)
                .setMaxResults(count)
                .getResultList()
                .stream()
                .map(tuple -> {
                    Movie movie = (Movie) tuple.get("movie");
                    return PopularMovieDto.builder()
                            .id(movie.getId())
                            .title(movie.getTitle())
                            .posterUrl(movie.getPosterUrl())
                            .reviewCount((Long) tuple.get("count"))
                            .build();
                })
                .sorted(Comparator.comparing(PopularMovieDto::getReviewCount))
                .toList();
    }
}
