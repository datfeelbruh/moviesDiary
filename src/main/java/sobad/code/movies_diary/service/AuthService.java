package sobad.code.movies_diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.authentication.AuthLoginRequest;
import sobad.code.movies_diary.authentication.AuthTokenResponse;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.jwts.JwtTokenUtils;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.repositories.TokenRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public AuthTokenResponse authenticate(AuthLoginRequest authLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginRequest.getUsername(),
                        authLoginRequest.getPassword()
                )
        );
        User user = userService.findByUsername(authLoginRequest.getUsername());

        String accessToken = jwtTokenUtils.generateAccessToken(user);
        String refreshToken = jwtTokenUtils.generateRefreshToken(user);
        saveUserToken(user, accessToken);

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthTokenResponse refreshToken(HttpServletRequest request) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String accessToken;
        final String username;
        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException();
        }
        refreshToken = authHeader.substring(7);
        username = jwtTokenUtils.extractUsername(refreshToken);
        if (username != null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            User user = userService.findByUsername(username);
            if (jwtTokenUtils.isTokenValid(refreshToken, userDetails)) {
                accessToken = jwtTokenUtils.generateAccessToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthTokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new RuntimeException();
    }
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByAccessToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            authentication.setAuthenticated(false);
            SecurityContextHolder.clearContext();

            AppError appError = new AppError(
                    FORBIDDEN.value(),
                    "Вы успешно вышли из профиля",
                    LocalDateTime.now().toString());

            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), appError);
        }
    }

    private void saveUserToken(User user, String accessToken) {
        log.info(accessToken);
        Token token = Token.builder()
                .user(userService.findByUsername(user.getUsername()))
                .accessToken(accessToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
