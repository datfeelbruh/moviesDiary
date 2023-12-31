package sobad.code.moviesdiary.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Table(name = "movies")
@Indexed
public class Movie {
    @Id
    @Column(name = "kp_id", unique = true)
    private Long id;
    @Column(name = "title")
    @FullTextField(name = "title")
    private String title;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "release_year")
    private Integer releaseYear;
    @Column(name = "kp_rating")
    private Double kpRating;
    @Column(name = "imdb_rating")
    private Double imdbRating;
    @Column(name = "kg_rating")
    private Double kgRating;
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
