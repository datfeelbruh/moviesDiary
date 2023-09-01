package sobad.code.moviesdiary.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.moviesdiary.ImageUtils;
import sobad.code.moviesdiary.dtos.user.UserDtoAboutRequest;
import sobad.code.moviesdiary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.PasswordException;
import sobad.code.moviesdiary.exceptions.UploadAvatarException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.CustomAccessDeniedException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityAlreadyExistException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.mappers.entity_serializers.UserSerializer;
import sobad.code.moviesdiary.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static sobad.code.moviesdiary.controllers.ImageController.IMAGE_CONTROLLER_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ImageUtils imageUtils;
    private final UserSerializer userSerializer;


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с таким именем '%s' не найден", username)
        ));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с таким ID '%s' не найден", id)
        ));
    }

    public UserDtoResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с данным ID не найден."));

        return userSerializer.apply(user);
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }

    @Transactional
    public UserDtoResponse createUser(UserRegistrationDtoRequest authRegistrationRequest) {
        Optional<User> userInDbWithUsername = userRepository.findByUsername(authRegistrationRequest.getUsername());
        Optional<User> userInDbWithEmail = userRepository.findByEmail(authRegistrationRequest.getEmail());
        if (userInDbWithUsername.isPresent() || userInDbWithEmail.isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с таким именем или email уже существует");
        }
        if (!Objects.equals(authRegistrationRequest.getPassword(), authRegistrationRequest.getConfirmPassword())) {
            throw new PasswordException("Пароль и потверждающий пароль не совпадают");
        }
        User user = new User();
        user.setEmail(authRegistrationRequest.getEmail());
        user.setUsername(authRegistrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRegistrationRequest.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        userRepository.save(user);

        return userSerializer.apply(user);
    }

    @Transactional
    public UserDtoResponse updateAbout(Long userId, UserDtoAboutRequest aboutRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с данным ID не найден."));
        User currentUser = getCurrentUser();
        if (!user.getId().equals(currentUser.getId())) {
            throw new CustomAccessDeniedException("Нельзя обновить данные другого пользователя!");
        }

        user.setId(userId);
        user.setAbout(aboutRequest.getAbout());
        userRepository.save(user);

        return userSerializer.apply(user);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден!"));
    }

    public UserDtoResponse uploadImage(MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String contentType = multipartFile.getContentType();
            if (contentType != null && ImageUtils.isSupportedContentType(contentType)) {
                User user = getCurrentUser();
                imageUtils.deletePreviousUserImage(user.getUsername());
                String filepath = imageUtils.buildFile(multipartFile.getContentType(), user.getUsername());
                multipartFile.transferTo(new File(filepath));
                String link = filepath.substring(filepath.lastIndexOf("/"));
                link = IMAGE_CONTROLLER_PATH + link;
                user.setAvatar(link);
                userRepository.save(user);
                return userSerializer.apply(user);
            }
            throw new UploadAvatarException("Неподдерживаемый тип файла.");
        }
        throw new UploadAvatarException("Файл пустой!");
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public Page<Movie> getUserFavorites(Long userId, PageRequest pageRequest) {
        return userRepository.getFavorites(userId, pageRequest);
    }

    public boolean isFavoriteMovieForCurrentUser(Long userId, Long movieId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFavorites().stream()
                .anyMatch(e -> e.getId().equals(movieId));
    }
}
