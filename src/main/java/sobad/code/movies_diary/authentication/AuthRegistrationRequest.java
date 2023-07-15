package sobad.code.movies_diary.authentication;

import lombok.Data;
import sobad.code.movies_diary.dto.Dto;

@Data
public class AuthRegistrationRequest implements Dto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
