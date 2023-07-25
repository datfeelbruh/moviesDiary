package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sobad.code.movies_diary.entities.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long>, QuerydslPredicateExecutor<Movie> {
    @Query("SELECT m.title FROM Movie m ORDER BY m.title LIMIT 20")
    List<String> findMoviesName();
    Optional<Movie> findById(Long id);
    List<Movie> findAll();
}
