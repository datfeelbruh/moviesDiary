package sobad.code.moviesdiary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.moviesdiary.entities.Token;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByAccessToken(String accessToken);
}
