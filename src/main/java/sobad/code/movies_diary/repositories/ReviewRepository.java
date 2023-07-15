package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.movies_diary.entities.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMovieId(Long id);
    Review findAllByUserIdAndMovieId(Long userId, Long movieId);
}
