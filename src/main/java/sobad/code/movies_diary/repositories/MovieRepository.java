package sobad.code.movies_diary.repositories;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sobad.code.movies_diary.entities.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long>, QuerydslPredicateExecutor<Movie> {
    Optional<Movie> findByMovieName(String name);
    List<Movie> findAllByKpId(Long id);
    Optional<Movie> findByKpId(Long id);
    List<Movie> findAll();
}
