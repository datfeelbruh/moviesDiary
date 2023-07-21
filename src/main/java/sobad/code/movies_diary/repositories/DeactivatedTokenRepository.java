package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.movies_diary.entities.DeactivatedToken;

import java.util.Optional;

public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, Long> {
    Optional<DeactivatedToken> findByToken(String token);
}
