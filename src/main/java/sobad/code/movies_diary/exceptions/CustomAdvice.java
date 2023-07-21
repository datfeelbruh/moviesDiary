package sobad.code.movies_diary.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice
@Slf4j
public class CustomAdvice {
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<AppError> sameUsernameOrEmail(DataIntegrityViolationException e) {
//        log.error(e.getMessage(), e);
//        return ResponseEntity
//                .status(422)
//                .body(new AppError(UNPROCESSABLE_ENTITY.value(), "Пользователь с таким именем"
//                        + " или email уже существует", LocalDateTime.now().toString()));
//    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<AppError> jwtExpired(ExpiredJwtException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(401)
                .body(new AppError(
                        UNAUTHORIZED.value(),
                        "Необходимо повторно авторизироваться",
                        LocalDateTime.now().toString())
                );
    }
    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<AppError> passwordMismatch(UserPasswordMismatchException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<AppError> authFailed(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new AppError(UNAUTHORIZED.value(), e.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<AppError> movieNotFound(MovieNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<AppError> reviewNotFound(ReviewNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), LocalDateTime.now().toString()));
    }
}
