package sobad.code.movies_diary.jwts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sobad.code.movies_diary.entities.BaseModel;
import sobad.code.movies_diary.entities.User;

@Data
@Builder
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Token extends BaseModel {
    @Column(unique = true)
    private String token;
    private boolean revoked;
    private boolean expired;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
