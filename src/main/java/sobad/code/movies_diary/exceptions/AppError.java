package sobad.code.movies_diary.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppError {
    private int statusCode;
    private String message;
    private String timestamp;
}
