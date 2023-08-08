package sobad.code.moviesdiary.repository;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Rollback
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;


    @Test
    void UserRepository_SaveUser_ReturnUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("email@gmail.com");

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void UserRepository_SaveUser_WithIncorrectAbout() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("emailgmail");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1500; i++) {
            sb.append(i);
        }

        String about = sb.toString();

        user.setAbout(about);

        Exception exception =  assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(user);
        });

        String message = exception.getMessage();

        Assertions.assertThat(message).isNotNull();
    }

    @Test
    void UserRepository_SaveUser_WithIncorrectEmail() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("emailgmail");

        Exception exception =  assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(user);
        });
        String message = exception.getMessage();

        Assertions.assertThat(message).isNotNull();
    }

    @Test
    void UserRepository_FindUserByEmail_ReturnUserWithCorrectEmail() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("email@gmail.com");

        User savedUser = userRepository.save(user);

        Optional<User> userWithEmail = userRepository.findByEmail(savedUser.getEmail());

        Assertions.assertThat(userWithEmail).isPresent();
        Assertions.assertThat(userWithEmail.get().getEmail()).isNotNull();
        Assertions.assertThat(userWithEmail.get().getEmail()).isEqualTo(savedUser.getEmail());
        Assertions.assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void UserRepository_FindUserByEmail_ReturnUserWithIncorrectEmail() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("email@gmail.com");

        userRepository.save(user);

        Optional<User> userWithEmail = userRepository.findByEmail("somemail@mail.com");

        Assertions.assertThat(userWithEmail).isEmpty();
    }

    @Test
    void UserRepository_FindUserByUserName_ReturnUserWithCorrectUserName() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("email@gmail.com");

        User savedUser = userRepository.save(user);

        Optional<User> userWithEmail = userRepository.findByUsername(savedUser.getUsername());

        Assertions.assertThat(userWithEmail).isPresent();
        Assertions.assertThat(userWithEmail.get().getEmail()).isNotNull();
        Assertions.assertThat(userWithEmail.get().getEmail()).isEqualTo(savedUser.getEmail());
        Assertions.assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void UserRepository_FindUserByUserName_ReturnUserWithIncorrectUserName() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setAbout("about");
        user.setEmail("email@gmail.com");

        userRepository.save(user);

        Optional<User> userWithUsername = userRepository.findByUsername("someusername");

        Assertions.assertThat(userWithUsername).isEmpty();
    }
}
