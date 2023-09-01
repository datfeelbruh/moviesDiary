package sobad.code.moviesdiary.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.dtos.ResponseMessage;
import sobad.code.moviesdiary.dtos.ResetPasswordDto;
import sobad.code.moviesdiary.dtos.tokens.ResetPasswordTokenDto;
import sobad.code.moviesdiary.entities.ResetPasswordToken;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.PasswordException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.repositories.ResetPasswordTokenRepository;
import sobad.code.moviesdiary.repositories.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    public ResetPasswordTokenDto createResetPasswordToken(String email)
            throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с данным email не найден."));

        Date createdAt = Date.from(Instant.now());
        Date expiredAt = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));
        String uuid = user.getEmail() + createdAt + expiredAt;

        ResetPasswordToken resetPasswordToken = ResetPasswordToken.builder()
                .token(UUID.nameUUIDFromBytes(uuid.getBytes()).toString())
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .user(user)
                .build();

        resetPasswordTokenRepository.save(resetPasswordToken);
        sendResetPasswordEmail("http://xn--b1abohqcebc.xn--p1ai/resetPassword?token="
                        + resetPasswordToken.getToken(), user.getEmail());
        return new ResetPasswordTokenDto(resetPasswordToken.getToken());
    }

    @Transactional
    public ResponseMessage updatePassword(ResetPasswordDto resetPasswordDto, String token) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token).orElseThrow();
        if (resetPasswordToken.getExpiredAt().before(Date.from(Instant.now()))) {
            throw new PasswordException("Истек токен для ресета пароля. "
                    + "Необходимо запросить смену пароля на почте повторно");
        }
        User user = resetPasswordToken.getUser();

        user.setId(user.getId());
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        userRepository.save(user);
        resetPasswordTokenRepository.delete(resetPasswordToken);
        return new ResponseMessage(200, "Пароль успешно изменен", Date.from(Instant.now()).toString());
    }

    private void sendResetPasswordEmail(String url, String email)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Сброс пароля на Movies Diary";
        String sender = "Movies Diary";
        String mailContent = "<a href=\"" + url + "\">Change password</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("movies.diary.team@gmail.com", sender);
        messageHelper.setTo(email);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

}
