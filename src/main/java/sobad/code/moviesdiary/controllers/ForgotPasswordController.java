package sobad.code.moviesdiary.controllers;

import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.moviesdiary.dtos.ResetPasswordDto;
import sobad.code.moviesdiary.exceptions.AppError;
import sobad.code.moviesdiary.services.ResetPasswordService;
import sobad.code.moviesdiary.services.UserService;

@RestController
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    public static final String API_FORGOT_PASSWORD_RESET = "/api/forgotPassword/reset";

    @Operation(summary = "Послать письмо на почту.", description =
            """
            Вернет токен ресета пароля.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Токен ресета пароля.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Невозможно сгенирировать токен для данного email.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @GetMapping(value = API_FORGOT_PASSWORD_RESET)
    public ResponseEntity<Map<String, Object>> sendResetRequest(@RequestParam
                                                  @Parameter(description = "Email адрес пользователя.",
                                                          example = "{name}@{mail}.domain") String email)
            throws MessagingException, UnsupportedEncodingException {
        return new ResponseEntity<>(resetPasswordService.createResetPasswordToken(email), OK);
    }
    @Operation(summary = "Запрос на ресет пароля.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщение об изменении пароля.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Токен ресета пароля истек.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class)
                            )
                    }
            )
    })
    @PostMapping(value = API_FORGOT_PASSWORD_RESET)
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                @RequestParam @Parameter(description = "Токен смены пароля.") String token) {
        return ResponseEntity.ok(resetPasswordService.updatePassword(resetPasswordDto, token));
    }
}
