package sobad.code.movies_diary.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

public class UserPasswordMismatchException extends RuntimeException {
    public UserPasswordMismatchException(String message) {
        super(message);
    }
}
