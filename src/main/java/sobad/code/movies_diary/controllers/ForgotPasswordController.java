package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dtos.ResetPasswordDto;
import sobad.code.movies_diary.dtos.authentication.AuthTokenDtoResponse;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.services.ResetPasswordService;
import sobad.code.movies_diary.services.UserService;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    public static final String FORGOT_PASSWORD_CONTROLLER_PATH_RESET_PASSWORD = "/api/forgotPassword/reset";
    @Operation(summary = "Послать письмо на почту.", description =
            """
            Вернет токен ресета пароля.
            """)
    @GetMapping(value = FORGOT_PASSWORD_CONTROLLER_PATH_RESET_PASSWORD)
    public ResponseEntity<?> sendResetRequest(@RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        return new ResponseEntity<>(resetPasswordService.createResetPasswordToken(email), OK);
    }
    @Operation(summary = "Запрос на ресет пароля.")
    @PostMapping(value = FORGOT_PASSWORD_CONTROLLER_PATH_RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                @RequestParam String token) {
        return ResponseEntity.ok(resetPasswordService.updatePassword(resetPasswordDto, token));
    }
}
