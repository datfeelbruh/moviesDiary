package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.movies_diary.entities.DeactivatedToken;

public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, Long> {
    Boolean existsByToken(String token);
}

