package sobad.code.movies_diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.movies_diary.entities.Token;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByRefreshToken(String refreshToken);
}
