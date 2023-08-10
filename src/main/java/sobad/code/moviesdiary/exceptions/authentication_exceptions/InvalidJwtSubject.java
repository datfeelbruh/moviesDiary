package sobad.code.moviesdiary.exceptions.authentication_exceptions;

public class InvalidJwtSubject extends RuntimeException {
    public InvalidJwtSubject(String message) {
        super(message);
    }
}
