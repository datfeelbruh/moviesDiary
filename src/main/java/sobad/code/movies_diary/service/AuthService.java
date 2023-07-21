package sobad.code.movies_diary.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.authentication.AuthLoginRequest;
import sobad.code.movies_diary.authentication.AuthTokenResponse;
import sobad.code.movies_diary.dto.RefreshTokenDto;
import sobad.code.movies_diary.entities.DeactivatedToken;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.RefreshTokenNotFound;
import sobad.code.movies_diary.jwts.JwtTokenUtils;
import sobad.code.movies_diary.jwts.JwtToken;
import sobad.code.movies_diary.entities.Token;
import sobad.code.movies_diary.mappers.AccessTokenSerializer;
import sobad.code.movies_diary.mappers.RefreshTokenSerializer;
import sobad.code.movies_diary.repositories.DeactivatedTokenRepository;
import sobad.code.movies_diary.repositories.TokenRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${jwt.expiration}")
    private long jwtAccessTokenExpiration;
    @Value(("${jwt.refresh-token.expiration}"))
    private long jwtRefreshTokenExpiration;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final DeactivatedTokenRepository deactivatedTokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AccessTokenSerializer accessTokenSerializer;
    private final RefreshTokenSerializer refreshTokenSerializer;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthTokenResponse authenticate(AuthLoginRequest authLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginRequest.getUsername(),
                        authLoginRequest.getPassword()
                )
        );
        UserDetails userDetails = userService.loadUserByUsername(authLoginRequest.getUsername());
        User user = userService.findByUsername(userDetails.getUsername());

        JwtToken accessToken = JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(userDetails.getUsername())
                .userId(user.getId())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, SECONDS))
                .build();


        JwtToken refreshToken = JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(userDetails.getUsername())
                .userId(user.getId())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(jwtRefreshTokenExpiration, HOURS))
                .build();

        String accessTokenString = accessTokenSerializer.apply(accessToken);
        String refreshTokenString = refreshTokenSerializer.apply(refreshToken);

        Token tokens = Token.builder()
                .accessToken(accessTokenString)
                .accessTokenExpiry(accessToken.getExpiredAt().toString())
                .refreshToken(refreshTokenString)
                .refreshTokenExpiry(refreshToken.getExpiredAt().toString())
                .build();

        tokenRepository.save(tokens);

        return AuthTokenResponse.builder()
                .accessToken(accessTokenString)
                .refreshToken(refreshTokenString)
                .build();
    }

    public AuthTokenResponse refreshToken(RefreshTokenDto refreshTokenDto) {
        if (deactivatedTokenRepository.findByToken(refreshTokenDto.getRefreshToken()).isPresent()) {
            throw new RuntimeException("ПОШЕЛ НАХУЙ ПИДАРАС");
        }
        String jwtToken = refreshTokenDto.getRefreshToken();
        String username = jwtTokenUtils.extractUsername(jwtToken);

        if (username != null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            User user = userService.findByUsername(username);

            if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)) {
                Token token = tokenRepository.findByRefreshToken(jwtToken);

                deactivatedTokenRepository.save(new DeactivatedToken(
                        token.getAccessToken(),
                        Date.from(Instant.now()))
                );
                deactivatedTokenRepository.save(new DeactivatedToken(
                        token.getRefreshToken(),
                        Date.from(Instant.now()))
                );

                Claims claims = jwtTokenUtils.extractAllClaims(jwtToken);
                String subject = jwtTokenUtils.extractUsername(jwtToken);
                List<String> roles = jwtTokenUtils.extractRoles(jwtToken);
                Long userId = claims.get("userId", Long.class);

                JwtToken accessToken = JwtToken.builder()
                        .id(UUID.randomUUID())
                        .subject(subject)
                        .userId(userId)
                        .authorities(roles)
                        .createdAt(Instant.now())
                        .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, SECONDS))
                        .build();


                JwtToken refreshToken = JwtToken.builder()
                        .id(UUID.randomUUID())
                        .subject(subject)
                        .userId(userId)
                        .authorities(roles)
                        .createdAt(Instant.now())
                        .expiredAt(Instant.now().plus(jwtRefreshTokenExpiration, HOURS))
                        .build();

                String accessTokenString = accessTokenSerializer.apply(accessToken);
                String refreshTokenString = refreshTokenSerializer.apply(refreshToken);

                Token tokens = Token.builder()
                        .accessToken(accessTokenString)
                        .accessTokenExpiry(accessToken.getExpiredAt().toString())
                        .refreshToken(refreshTokenString)
                        .refreshTokenExpiry(refreshToken.getExpiredAt().toString())
                        .build();

                tokenRepository.save(tokens);

                return AuthTokenResponse.builder()
                        .accessToken(accessTokenString)
                        .refreshToken(refreshTokenString)
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
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return;
//        }
//        jwt = authHeader.substring(7);
//        var storedToken = refreshTokenRepository.findByAccessToken(jwt)
//                .orElse(null);
//        if (storedToken != null) {
//            storedToken.setExpired(true);
//            refreshTokenRepository.save(storedToken);
//            authentication.setAuthenticated(false);
//            SecurityContextHolder.clearContext();
//
//            AppError appError = new AppError(
//                    FORBIDDEN.value(),
//                    "Вы успешно вышли из профиля",
//                    LocalDateTime.now().toString());
//
//            response.setStatus(FORBIDDEN.value());
//            response.setContentType(APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding(UTF_8.toString());
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            objectMapper.writeValue(response.getWriter(), appError);
    }

}
