package sobad.code.movies_diary.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {
    private Long id;
    private String username;
    private String email;
}
