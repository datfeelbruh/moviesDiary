package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
