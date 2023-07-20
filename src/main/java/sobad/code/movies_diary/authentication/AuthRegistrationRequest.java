package sobad.code.movies_diary.authentication;

import lombok.Data;

@Data
public class AuthRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
