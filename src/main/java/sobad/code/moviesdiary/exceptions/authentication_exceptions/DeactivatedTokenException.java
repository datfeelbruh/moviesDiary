package sobad.code.moviesdiary.exceptions.authentication_exceptions;

public class DeactivatedTokenException extends RuntimeException {
    public DeactivatedTokenException(String message) {
        super(message);
    }
}
