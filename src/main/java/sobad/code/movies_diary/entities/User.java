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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sobad.code.movies_diary.jwts.JwtToken;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
//    @Column(name = "about")
//    private String about;
    @Column(name = "password", nullable = false)
    private String password;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Collection<Role> roles;
}
