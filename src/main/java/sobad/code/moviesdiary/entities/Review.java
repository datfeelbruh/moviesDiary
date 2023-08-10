package sobad.code.moviesdiary.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@Entity
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "reviews")
public class Review extends BaseModel {
    @Column(name = "rating")
    @DecimalMin(value = "0.0", message = "Рейтинг не может быть меньше 0.0")
    @DecimalMax(value = "10.0", message = "Рейтинг не может быть больше 10.0")
    private Double rating;
    @Column(name = "userReview", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Ревью слишком большое, краткость сестра таланта.")
    private String userReview;
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
