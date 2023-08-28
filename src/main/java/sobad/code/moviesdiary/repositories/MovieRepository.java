package sobad.code.moviesdiary.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sobad.code.moviesdiary.entities.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long>, QuerydslPredicateExecutor<Movie> {
    Optional<Movie> findById(Long id);
    List<Movie> findAll();
}
