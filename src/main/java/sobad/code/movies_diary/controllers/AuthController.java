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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.authentication.AuthLoginRequest;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;

import sobad.code.movies_diary.authentication.AuthTokenResponse;
import sobad.code.movies_diary.dto.UserDto;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.service.AuthService;
import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация и авторизация")
public class AuthController {
    private final AuthService authService;
    public static final String AUTH_CONTROLLER_REG_PATH = "/api/auth/registration";
    public static final String AUTH_CONTROLLER_LOGIN_PATH = "/api/auth/login";
    public static final String AUTH_CONTROLLER_REFRESH_TOKEN_PATH = "/api/auth/refresh";
    public static final String AUTH_CONTROLLER_LOGOUT_PATH = "/api/auth/logout";

    @Operation(summary = "Регистрация пользователя в приложении"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Данные пользователя", content =
            @Content(schema =
            @Schema(implementation = UserDto.class))
            )
    })
    @PostMapping(AUTH_CONTROLLER_REG_PATH)
    public ResponseEntity<?> registry(@RequestBody AuthRegistrationRequest authRegistrationRequest) {
        try {
            return new ResponseEntity<>(authService.registry(authRegistrationRequest), CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    String.format("Пользователь с таким именем '%s' уже существует!",
                            authRegistrationRequest.getUsername())),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Operation(summary = "Аутентификация пользователя в приложении"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Токены аутентификации", content =
            @Content(schema =
            @Schema(implementation = Token.class))
            )
    })
    @PostMapping(AUTH_CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest authLoginRequest) {
        try {
            return ResponseEntity.ok(authService.authenticate(authLoginRequest));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(),
                    "Не удалось авторизироваться с такими данными"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Получение нового токена доступа"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Токены аутентификации", content =
            @Content(schema =
            @Schema(implementation = Token.class))
            )
    })
    @PostMapping(AUTH_CONTROLLER_REFRESH_TOKEN_PATH)
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws IOException {
        try {
            AuthTokenResponse response = authService.refreshToken(request);
            return new ResponseEntity<>(response, OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Рефреш токен невалиден"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Логаут он и в Африке логаут"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Веселое сообщение :)")
    })
    @GetMapping(AUTH_CONTROLLER_LOGOUT_PATH)
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        authService.logout(request, response, authentication);
    }

}
