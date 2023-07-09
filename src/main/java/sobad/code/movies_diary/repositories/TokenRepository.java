package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.movies_diary.jwts.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllValidTokenByUserId(long id);

    Optional<Token> findByToken(String token);
}
