package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class DeactivatedTokenException extends RuntimeException {
    public DeactivatedTokenException(String message) {
        super(message);
    }
}
