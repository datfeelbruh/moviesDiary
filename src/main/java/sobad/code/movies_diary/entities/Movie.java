package sobad.code.movies_diary.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movies")
public class Movie extends BaseModel {
    @Column(name = "kp_id", unique = true)
    private Long kpId;
    @Column(name = "movie_name")
    private String movieName;
    @Column(name = "release_year")
    private Integer releaseYear;
    @Column(name = "review")
    private String review;
    @Column(name = "kp_rating")
    private Double kpRating;
    @Column(name = "imdb_rating")
    private Double imdbRating;
    @Column(name = "poster_url")
    private String posterUrl;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movies_id"),
        inverseJoinColumns = @JoinColumn(name = "genres_id")
    )
    private Set<Genre> genres;
}
