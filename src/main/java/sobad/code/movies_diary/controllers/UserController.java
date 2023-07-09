package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.UserDto;
import sobad.code.movies_diary.dto.UserDtoResponse;
import sobad.code.movies_diary.repositories.UserRepository;
import sobad.code.movies_diary.service.UserService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с пользователем")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    public static final String USER_CONTROLLER_USER_PROFILE = "/api/users/{username}";
    public static final String USER_CONTROLLER_USER_CREATE = "/api/users";

    @Operation(summary = "Получить профиль пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя", content =
            @Content(schema =
            @Schema(implementation = UserDtoResponse.class))
            )
    })
    @GetMapping(USER_CONTROLLER_USER_PROFILE)
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        try {
            UserDtoResponse userDtoResponse = userService.getUserProfile(username);
            return new ResponseEntity<>(userDtoResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            String.format(
                                    "Такого пользователя '%s' не существует",
                                    username
                            )),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Регистрация пользователя в приложении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь", content =
            @Content(schema =
            @Schema(implementation = UserDto.class))
            )
    })
    @PostMapping(USER_CONTROLLER_USER_CREATE)
    public ResponseEntity<?> createNewUser(@RequestBody AuthRegistrationRequest registrationUserDto) {
        try {
            UserDto userDto = userService.createUser(registrationUserDto);
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            String.format(
                                    "Пользователь с таким именем уже существует",
                                    registrationUserDto.getUsername()
                            )),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(USER_CONTROLLER_USER_CREATE + "/current")
    public void current(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s1 = SecurityContextHolder.getContext().getAuthentication().getName();
        String s2 = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        response.getWriter().println(s1 + " " + s2);
    }
}
