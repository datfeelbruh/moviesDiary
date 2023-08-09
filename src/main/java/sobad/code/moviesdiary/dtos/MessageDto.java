package sobad.code.moviesdiary.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    private int statusCode;
    private String message;
    private String timestamp;
}
