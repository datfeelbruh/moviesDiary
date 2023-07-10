package sobad.code.movies_diary.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie_rating")
public class MovieRating extends BaseModel {
    @Column(name = "rating")
    private Double rating;
    @ManyToOne
    private Movie movie;
    @ManyToOne
    private User user;
}
