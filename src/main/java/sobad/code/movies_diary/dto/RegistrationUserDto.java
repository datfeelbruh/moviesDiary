package sobad.code.movies_diary.dto;

import lombok.Data;

@Data
public class RegistrationUserDto implements Dto {
    private String username;
    private String password;
    private String confirmPassword;
}
