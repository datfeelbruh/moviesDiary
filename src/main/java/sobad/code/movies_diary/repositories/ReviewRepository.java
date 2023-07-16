package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobad.code.movies_diary.entities.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMovieId(Long id);
    List<Review> findAllByUserId(Long id);
    Review findAllByUserIdAndMovieId(Long userId, Long movieId);
    Integer countByMovieId(Long movieId);
    @Query("select avg(rating) from Review r where r.movie.id = :id")
    Double getAvgRatingByMovieId(@Param("id") Long movieId);
}
