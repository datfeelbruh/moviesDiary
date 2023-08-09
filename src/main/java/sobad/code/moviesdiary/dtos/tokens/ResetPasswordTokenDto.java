package sobad.code.moviesdiary.dtos.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordTokenDto {
    private String resetToken;
}
