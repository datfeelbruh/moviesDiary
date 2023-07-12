package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.UserDto;
import sobad.code.movies_diary.dto.UserDtoResponse;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.UserPasswordMismatchException;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final MovieRatingService movieRatingService;
    private final MovieMapper movieMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDtoResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username).get();
        return UserDtoResponse.builder()
                .username(username)
                .movies(user.getMovies().stream()
                        .map(movieMapper::mapFromEntityToResponseDto)
                        .peek(e -> {
                            Double userRating = movieRatingService.getRatingById(e.getId(), user.getId());
                            e.setUserRating(userRating);
                            e.setAverageRating(movieRatingService.calcAverageRating(e.getId()).orElse(userRating));
                        })
                        .collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с таким именем '%s' не найден", username)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    public UserDto createUser(AuthRegistrationRequest authRegistrationRequest) {
        if (!Objects.equals(authRegistrationRequest.getPassword(), authRegistrationRequest.getConfirmPassword())) {
            throw new UserPasswordMismatchException("Пароль и потверждающие пароль не совпадают");
        }
        User user = new User();
        user.setUsername(authRegistrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRegistrationRequest.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        user.setMovies(new HashSet<>());
        user.setUserMovieRatings(new HashSet<>());

        userRepository.save(user);
        return new UserDto(user.getId(), user.getUsername());
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).get();
    }

    public void addMovieToUser(Movie movie) {
        User user = getCurrentUser();
        Set<Movie> set = new HashSet<>(user.getMovies());
        set.add(movie);
        user.setMovies(set);
        userRepository.save(user);
    }
}
