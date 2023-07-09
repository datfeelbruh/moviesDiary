package sobad.code.movies_diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.authentication.AuthLoginRequest;
import sobad.code.movies_diary.authentication.AuthTokenResponse;
import sobad.code.movies_diary.authentication.AuthRegistrationRequest;
import sobad.code.movies_diary.dto.UserDto;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.jwts.JwtTokenUtils;
import sobad.code.movies_diary.jwts.Token;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.repositories.UserRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public UserDto registry(AuthRegistrationRequest registrationUserDto) {
       return userService.createUser(registrationUserDto);
    }

    public AuthTokenResponse authenticate(AuthLoginRequest authLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginRequest.getUsername(),
                        authLoginRequest.getPassword()
                )
        );
        User user = userService.findByUsername(authLoginRequest.getUsername()).orElseThrow();

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
            User user = userService.findByUsername(username).orElseThrow();
            if (jwtTokenUtils.isTokenValid(refreshToken, user)) {
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
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            authentication.setAuthenticated(false);
            SecurityContextHolder.clearContext();

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "You have successfully logged out");
            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), errorDetails);
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
