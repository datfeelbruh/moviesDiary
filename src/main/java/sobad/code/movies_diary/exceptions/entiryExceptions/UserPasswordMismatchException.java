package sobad.code.movies_diary.exceptions.entiryExceptions;

public class UserPasswordMismatchException extends RuntimeException {
    public UserPasswordMismatchException(String message) {
        super(message);
    }
}
