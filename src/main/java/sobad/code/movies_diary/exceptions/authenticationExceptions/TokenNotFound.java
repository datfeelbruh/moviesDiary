package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class TokenNotFound extends RuntimeException {
    public TokenNotFound(String message) {
        super(message);
    }
}
