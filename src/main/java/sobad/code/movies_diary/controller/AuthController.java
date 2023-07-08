package sobad.code.movies_diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dto.RegistrationUserDto;
import sobad.code.movies_diary.dto.UserDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.dto.jwts.JwtRequest;
import sobad.code.movies_diary.dto.jwts.JwtResponse;
import sobad.code.movies_diary.service.AuthService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация и авторизация")
public class AuthController {
    private final AuthService authService;
    public static final String AUTH_CONTROLLER_REG_PATH = "/api/registration";
    public static final String AUTH_CONTROLLER_LOGIN_PATH = "/api/login";

    @Operation(summary = "Аутентификация пользователя в приложении"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен аутентификаии", content =
            @Content(schema =
            @Schema(implementation = JwtResponse.class))
            )
    })
    @PostMapping(AUTH_CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @Operation(summary = "Регистрация пользователя в приложении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь", content =
            @Content(schema =
            @Schema(implementation = UserDto.class))
            )
    })
    @PostMapping(AUTH_CONTROLLER_REG_PATH)
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }
}
