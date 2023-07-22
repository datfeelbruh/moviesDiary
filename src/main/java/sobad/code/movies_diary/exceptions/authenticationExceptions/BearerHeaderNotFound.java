package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class BearerHeaderNotFound extends RuntimeException {
    public BearerHeaderNotFound(String message) {
        super(message);
    }
}
