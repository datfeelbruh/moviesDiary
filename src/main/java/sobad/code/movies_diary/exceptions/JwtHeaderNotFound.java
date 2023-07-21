package sobad.code.movies_diary.exceptions;

public class JwtHeaderNotFound extends RuntimeException {
    public JwtHeaderNotFound(String message) {
        super(message);
    }
}
