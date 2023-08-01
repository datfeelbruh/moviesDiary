package sobad.code.movies_diary.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import sobad.code.movies_diary.exceptions.authenticationExceptions.DeactivatedTokenException;
import sobad.code.movies_diary.exceptions.entiryExceptions.CustomAccessDeniedException;
import sobad.code.movies_diary.exceptions.entiryExceptions.MovieNotFoundException;
import sobad.code.movies_diary.exceptions.entiryExceptions.ReviewNotFoundException;
import sobad.code.movies_diary.exceptions.entiryExceptions.UpdateAnotherUserDataException;
import sobad.code.movies_diary.exceptions.entiryExceptions.UserAlreadyExistException;
import sobad.code.movies_diary.exceptions.entiryExceptions.UserNotFoundException;
import sobad.code.movies_diary.exceptions.entiryExceptions.UserPasswordMismatchException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice
@Slf4j
public class CustomAdvice {
    @ExceptionHandler(UpdateAnotherUserDataException.class)
    public ResponseEntity<AppError> updateAnotherUser(UpdateAnotherUserDataException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppError> userNotFound(UserNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<AppError> avatarUploadSizeException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        "Размер файла слишком большой! Максимальный размер для загружаемого файла 8 МБ.",
                        Instant.now().toString()));
    }

    @ExceptionHandler(UploadAvatarException.class)
    public ResponseEntity<AppError> avatarUploadException(UploadAvatarException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }

    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<AppError> avatarNotFoundException(AvatarNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        NOT_FOUND.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppError> validationException(ConstraintViolationException e) {
        List<String> errorMessages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage).toList();
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), errorMessages.get(0), Instant.now().toString()));
    }

    @ExceptionHandler(DeactivatedTokenException.class)
    public ResponseEntity<AppError> deactivatedTokenException(DeactivatedTokenException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(new AppError(FORBIDDEN.value(), e.getMessage(), Instant.now().toString()));
    }
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<AppError> deleteReviewAnotherUser(CustomAccessDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(new AppError(FORBIDDEN.value(), e.getMessage(), Instant.now().toString()));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<AppError> sameUsernameOrEmail(UserAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), "Пользователь с таким именем"
                        + " или email уже существует", Instant.now().toString()));
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<AppError> jwtExpired(ExpiredJwtException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(401)
                .body(new AppError(
                        UNAUTHORIZED.value(),
                        "Необходимо повторно авторизироваться",
                        Instant.now().toString())
                );
    }
    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<AppError> passwordMismatch(UserPasswordMismatchException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), Instant.now().toString()));
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<AppError> authFailed(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new AppError(UNAUTHORIZED.value(), e.getMessage(), Instant.now().toString()));
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<AppError> movieNotFound(MovieNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), Instant.now().toString()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<AppError> reviewNotFound(ReviewNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), Instant.now().toString()));
    }
}
