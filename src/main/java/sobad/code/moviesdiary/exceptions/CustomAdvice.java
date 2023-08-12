package sobad.code.moviesdiary.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.DeactivatedTokenException;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.CustomJwtExpiredException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.CustomAccessDeniedException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityAlreadyExistException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice
@Slf4j
public class CustomAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppError> runtimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<AppError> entityNotFound(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        UNPROCESSABLE_ENTITY.value(),
                        e.getMessage(),
                        Instant.now().toString()));
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<AppError> entityAlreadyExist(EntityAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage(), Instant.now().toString()));
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

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<AppError> customAccessDenied(CustomAccessDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(new AppError(FORBIDDEN.value(), e.getMessage(), Instant.now().toString()));
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<AppError> passwordException(PasswordException e) {
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
                .body(new AppError(UNAUTHORIZED.value(),
                        "Пользователь с такими данными не существует",
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

    @ExceptionHandler(DeactivatedTokenException.class)
    public ResponseEntity<AppError> deactivatedTokenException(DeactivatedTokenException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(new AppError(FORBIDDEN.value(), e.getMessage(), Instant.now().toString()));
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

    @ExceptionHandler(CustomJwtExpiredException.class)
    public ResponseEntity<AppError> customJwtException(CustomJwtExpiredException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(401)
                .body(new AppError(
                        UNAUTHORIZED.value(),
                        e.getMessage(),
                        Instant.now().toString())
                );
    }
}
