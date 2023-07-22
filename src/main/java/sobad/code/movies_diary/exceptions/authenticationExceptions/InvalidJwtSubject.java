package sobad.code.movies_diary.exceptions.authenticationExceptions;

public class InvalidJwtSubject extends RuntimeException {
    public InvalidJwtSubject(String message) {
        super(message);
    }
}
