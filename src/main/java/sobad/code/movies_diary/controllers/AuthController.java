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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dto.RefreshTokenDto;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.authentication.AuthLoginRequest;

import sobad.code.movies_diary.authentication.AuthTokenResponse;
import sobad.code.movies_diary.service.AuthService;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация и авторизация", description = "API Аутентификации пользователей")
public class AuthController {
    private final AuthService authService;
    public static final String AUTH_CONTROLLER_LOGIN_PATH = "/api/auth/login";
    public static final String AUTH_CONTROLLER_REFRESH_TOKEN_PATH = "/api/auth/refresh";
    public static final String AUTH_CONTROLLER_LOGOUT_PATH = "/api/auth/logout";

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Аутентификация успешна",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Не удалось авторизироваться с такими данными",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @PostMapping(AUTH_CONTROLLER_LOGIN_PATH)
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest authLoginRequest) {
        return ResponseEntity.ok(authService.authenticate(authLoginRequest));
    }

    @Operation(summary = "Получение нового токена доступа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Токены для этого пользователя успешно обновлненны",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Рефреш токен истек, нужно залогиниться заново",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @GetMapping(AUTH_CONTROLLER_REFRESH_TOKEN_PATH)
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws IOException {
        AuthTokenResponse response = authService.refreshToken(request);
        return new ResponseEntity<>(response, OK);
    }

    @Operation(summary = "Логаут пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно вышел")
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
