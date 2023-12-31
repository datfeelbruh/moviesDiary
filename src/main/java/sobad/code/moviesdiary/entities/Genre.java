package sobad.code.moviesdiary.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "genres")
public class Genre extends BaseModel {
    @Column(name = "name", unique = true)
    private String name;
}
