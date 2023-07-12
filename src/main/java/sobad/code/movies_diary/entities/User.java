package sobad.code.movies_diary.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import sobad.code.movies_diary.jwts.Token;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
        CascadeType.ALL
    })
    private Set<Movie> movies = new HashSet<>();
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<MovieRating> userMovieRatings;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Collection<Role> roles;
}
