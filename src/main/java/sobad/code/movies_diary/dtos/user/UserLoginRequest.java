package sobad.code.movies_diary.dtos.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
