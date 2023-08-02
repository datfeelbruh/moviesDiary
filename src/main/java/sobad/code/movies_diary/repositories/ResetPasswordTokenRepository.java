package sobad.code.movies_diary.repositories;

import org.springframework.data.repository.CrudRepository;
import sobad.code.movies_diary.entities.ResetPasswordToken;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends CrudRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
}
