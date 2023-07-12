package sobad.code.movies_diary.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import sobad.code.movies_diary.AppError;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice
@Slf4j
public class AuthControllerHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppError> sameUsername(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), "Пользователь с таким именем уже существует"));
    }

    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<AppError> passwordMismatch(UserPasswordMismatchException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), e.getMessage()));
    }
}
