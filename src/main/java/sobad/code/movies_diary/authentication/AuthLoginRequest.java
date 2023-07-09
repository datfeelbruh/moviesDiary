package sobad.code.movies_diary.authentication;

import lombok.Data;

@Data
public class AuthLoginRequest {
    private String username;
    private String password;
}
