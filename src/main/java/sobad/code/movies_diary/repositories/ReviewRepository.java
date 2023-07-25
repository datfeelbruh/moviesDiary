package sobad.code.movies_diary.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobad.code.movies_diary.entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMovieId(Long id, PageRequest pageRequest);
    Page<Review> findAllByUserId(Long id, PageRequest pageRequest);
    Optional<Review> findAllByUserIdAndMovieId(Long userId, Long movieId);
    @Query("SELECT r FROM Review r WHERE r.movie.id = :id ORDER BY id DESC LIMIT 4")
    List<Review> findRandomReviewByMovieId(@Param("id") Long movieId);
    Integer countByMovieId(Long movieId);
    @Query("SELECT avg(rating) FROM Review r WHERE r.movie.id = :id")
    Double getAvgRatingByMovieId(@Param("id") Long movieId);
}
