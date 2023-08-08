package sobad.code.moviesdiary.repositories;

import org.springframework.data.repository.CrudRepository;
import sobad.code.moviesdiary.entities.ResetPasswordToken;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends CrudRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
}
