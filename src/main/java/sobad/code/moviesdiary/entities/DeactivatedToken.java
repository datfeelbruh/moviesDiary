package sobad.code.moviesdiary.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deactivated_tokens")
public class DeactivatedToken extends BaseModel {
    private String token;
    private Date timestamp;
}
