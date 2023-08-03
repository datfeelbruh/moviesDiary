package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class CustomJwtExpiredException extends RuntimeException {
    public CustomJwtExpiredException(String message) {
        super(message);
    }
}
