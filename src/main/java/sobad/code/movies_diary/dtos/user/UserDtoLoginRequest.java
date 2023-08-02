package sobad.code.movies_diary.dtos.user;

import lombok.Data;

@Data
public class UserDtoLoginRequest {
    private String username;
    private String password;
}
