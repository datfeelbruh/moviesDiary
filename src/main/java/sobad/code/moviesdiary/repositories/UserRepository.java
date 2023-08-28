package sobad.code.moviesdiary.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query(value = "SELECT u.favorites FROM User u WHERE u.id = :id")
    Page<Movie> getFavorites(Long id, Pageable pageable);
}
