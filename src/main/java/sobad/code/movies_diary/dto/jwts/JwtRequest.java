package sobad.code.movies_diary.dto.jwts;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
