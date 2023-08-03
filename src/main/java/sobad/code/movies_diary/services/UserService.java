package sobad.code.movies_diary.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.movies_diary.ImageUtils;
import sobad.code.movies_diary.dtos.user.UserDtoAboutRequest;
import sobad.code.movies_diary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.movies_diary.dtos.user.UserDtoResponse;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.PasswordException;
import sobad.code.movies_diary.exceptions.UploadAvatarException;
import sobad.code.movies_diary.exceptions.entiryExceptions.CustomAccessDeniedException;
import sobad.code.movies_diary.exceptions.entiryExceptions.EntityAlreadyExistException;
import sobad.code.movies_diary.exceptions.entiryExceptions.EntityNotFoundException;
import sobad.code.movies_diary.mappers.entitySerializers.UserSerializer;
import sobad.code.movies_diary.repositories.ResetPasswordTokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static sobad.code.movies_diary.controllers.ImageController.IMAGE_CONTROLLER_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
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
                        .collect(Collectors.toList())
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
            throw new PasswordException("Пароль и потверждающие пароль не совпадают");
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
        return userRepository.findByUsername(username).get();
    }

    public UserDtoResponse uploadImage(MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty() && multipartFile != null) {
            if (isSupportedContentType(multipartFile.getContentType())) {
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

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/gif")
                || contentType.equals("image/jpeg");
    }
}
