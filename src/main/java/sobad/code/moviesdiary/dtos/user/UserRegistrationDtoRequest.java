package sobad.code.moviesdiary.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDtoRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
