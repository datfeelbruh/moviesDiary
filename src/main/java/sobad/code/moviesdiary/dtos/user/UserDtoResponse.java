package sobad.code.moviesdiary.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String about;
}
