package sobad.code.movies_diary.service;


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
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.user.UserDtoResponse;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.UserPasswordMismatchException;
import sobad.code.movies_diary.repositories.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с таким именем '%s' не найден", username)
        ));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с таким ID '%s' не найден", id)
        ));
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

    public UserDtoResponse createUser(AuthRegistrationRequest authRegistrationRequest) {
        if (!Objects.equals(authRegistrationRequest.getPassword(), authRegistrationRequest.getConfirmPassword())) {
            throw new UserPasswordMismatchException("Пароль и потверждающие пароль не совпадают");
        }
        User user = new User();
        user.setEmail(authRegistrationRequest.getEmail());
        user.setUsername(authRegistrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRegistrationRequest.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));

        userRepository.save(user);
        return new UserDtoResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).get();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        log.info(file.getContentType());
        log.info(file.getName());
        log.info(file.getOriginalFilename());
        log.info("bytes = ", file.getBytes());
        FileOutputStream stream = new FileOutputStream("C:/Users/datfe/Pictures/service-images/test.txt");
        try {
            stream.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "pizdec";
    }
}
