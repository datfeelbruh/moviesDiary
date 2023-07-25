package sobad.code.movies_diary.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    private UUID id;
    private String subject;
    private Long userId;
    private List<String> authorities;
    private Instant createdAt;
    private Instant expiredAt;
}
