package sobad.code.moviesdiary.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import sobad.code.moviesdiary.dtos.user.UserDtoAboutRequest;
import sobad.code.moviesdiary.dtos.user.UserDtoResponse;
import sobad.code.moviesdiary.dtos.user.UserRegistrationDtoRequest;
import sobad.code.moviesdiary.entities.Role;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.mappers.entity_serializers.UserSerializer;
import sobad.code.moviesdiary.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserSerializer userSerializer;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @Test
    void findByUsername_ReturnsValidUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");
        user.setRoles(List.of(new Role("ROLE_USER")));
        doReturn(Optional.of(user)).when(this.userRepository).findByUsername(user.getUsername());

        User response = userService.findByUsername(user.getUsername());

        assertNotNull(response);
    }

    @Test
    void findById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");
        user.setRoles(List.of(new Role("ROLE_USER")));
        doReturn(Optional.of(user)).when(this.userRepository).findById(user.getId());

        User response = userService.findById(user.getId());

        assertNotNull(response);
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");
        user.setRoles(List.of(new Role("ROLE_USER")));
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(
            UserDtoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .about(user.getAbout())
                    .avatar(user.getAvatar())
                    .build()
        ).when(userSerializer).apply(user);

        UserDtoResponse response = userService.getUserById(user.getId());
        assertNotNull(response);
    }

    @Test
    void createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");

        UserRegistrationDtoRequest userRegDto = UserRegistrationDtoRequest.builder()
                .username("username")
                .password("password")
                .confirmPassword("password")
                .email("email@mail.com")
                .build();
        UserDtoResponse returns = UserDtoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .about(user.getAbout())
                .avatar(user.getAvatar())
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userSerializer.apply(Mockito.any(User.class))).thenReturn(returns);
        doReturn("encodedPassword").when(passwordEncoder).encode(user.getPassword());
        doReturn(new Role("ROLE_USER")).when(roleService).getUserRole();

        UserDtoResponse response = userService.createUser(userRegDto);
        assertNotNull(response);
    }

    @Test
    void updateAbout() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");

        UserDtoAboutRequest userDtoAboutRequest = new UserDtoAboutRequest("about");
        UserDtoResponse returns = UserDtoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .about(user.getAbout())
                .avatar(user.getAvatar())
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userSerializer.apply(Mockito.any(User.class))).thenReturn(returns);

        UserDtoResponse response = userService.updateAbout(user.getId(), userDtoAboutRequest);
        assertNotNull(response);
        assertThat(response.getAbout()).isEqualTo(userDtoAboutRequest.getAbout());
    }

    @Test
    void getCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setAbout("about");
        user.setPassword("password");
        user.setEmail("email@mail.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");

        doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());

        User currentUser = userService.getCurrentUser();

        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo(user.getUsername());
    }
}