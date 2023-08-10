package sobad.code.moviesdiary.exceptions.authentication_exceptions;

public class CustomJwtExpiredException extends RuntimeException {
    public CustomJwtExpiredException(String message) {
        super(message);
    }
}
