package sobad.code.moviesdiary.exceptions.authentication_exceptions;

public class BearerHeaderNotFound extends RuntimeException {
    public BearerHeaderNotFound(String message) {
        super(message);
    }
}
