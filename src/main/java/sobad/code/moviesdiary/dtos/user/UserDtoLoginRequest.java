package sobad.code.moviesdiary.dtos.user;

import lombok.Data;

@Data
public class UserDtoLoginRequest {
    private String username;
    private String password;
}
