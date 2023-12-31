package sobad.code.moviesdiary.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "users")
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class User extends BaseModel {
    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Email должен соотвестновать паттерну *@*.*")
    private String email;
    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Введите имя пользователя чтобы зарегистрироваться")
    private String username;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "about", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Описание слишком большое, краткость сестра таланта.")
    private String about;
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Введите пароль чтобы зарегистрироваться")
    private String password;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Collection<Role> roles;
    @OneToMany
    private Set<Movie> favorites = new HashSet<>();
}
