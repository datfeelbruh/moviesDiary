package sobad.code.movies_diary.exceptions;

public class RefreshTokenNotFound extends RuntimeException {
    public RefreshTokenNotFound(String message) {
        super(message);
    }
}
