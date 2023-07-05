package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sobad.code.movies_diary.entities.MovieRating;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {
    List<MovieRating> findAllByMovieId(Long id);
    Optional<MovieRating> findByMovieIdAndUserId(Long movieId, Long userId);
}
