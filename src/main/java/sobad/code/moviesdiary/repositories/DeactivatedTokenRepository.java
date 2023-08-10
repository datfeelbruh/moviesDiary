package sobad.code.moviesdiary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.moviesdiary.entities.DeactivatedToken;

public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, Long> {
    Boolean existsByToken(String token);
}

