package sobad.code.moviesdiary.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.dtos.user.UserDtoLoginRequest;
import sobad.code.moviesdiary.dtos.authentication.AuthTokenDtoResponse;
import sobad.code.moviesdiary.entities.DeactivatedToken;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.AppError;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.BearerHeaderNotFound;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.DeactivatedTokenException;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.InvalidJwtSubject;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.jwt.JwtTokenUtils;
import sobad.code.moviesdiary.jwt.JwtToken;
import sobad.code.moviesdiary.entities.Token;
import sobad.code.moviesdiary.mappers.token_serializer.AccessTokenSerializer;
import sobad.code.moviesdiary.repositories.DeactivatedTokenRepository;
import sobad.code.moviesdiary.repositories.TokenRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${jwt.expiration}")
    private long jwtAccessTokenExpiration;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final DeactivatedTokenRepository deactivatedTokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AccessTokenSerializer accessTokenSerializer;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthTokenDtoResponse authenticate(UserDtoLoginRequest authLoginRequest) {
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
                .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, DAYS))
                .build();


        String accessTokenString = accessTokenSerializer.apply(accessToken);

        Token tokens = Token.builder()
                .accessToken(accessTokenString)
                .accessTokenExpiry(accessToken.getExpiredAt().toString())
                .build();

        tokenRepository.save(tokens);

        return AuthTokenDtoResponse.builder()
                .accessToken(accessTokenString)
                .build();
    }

    public AuthTokenDtoResponse refreshToken(HttpServletRequest request) throws IOException {
        final String header = request.getHeader(AUTHORIZATION);
        final String jwtToken = header.substring(7);

        String username = jwtTokenUtils.extractUsername(jwtToken);

        if (username != null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)
                    && Boolean.FALSE.equals(deactivatedTokenRepository.existsByToken(jwtToken))) {
                deactivatedTokenRepository.save(new DeactivatedToken(
                        jwtToken,
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
                        .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, DAYS))
                        .build();


                String accessTokenString = accessTokenSerializer.apply(accessToken);

                Token tokens = Token.builder()
                        .accessToken(accessTokenString)
                        .accessTokenExpiry(accessToken.getExpiredAt().toString())
                        .build();

                tokenRepository.save(tokens);

                return AuthTokenDtoResponse.builder()
                        .accessToken(accessTokenString)
                        .build();
            } else {
                throw new DeactivatedTokenException("Данный токен деактивирован, "
                        + "залогиньтесь заного чтобы получить корректный токен.");
            }
        }
        throw new InvalidJwtSubject("Имя пользователя в токене null.");
    }


    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        final String header = request.getHeader(AUTHORIZATION);
        final String jwtToken;
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BearerHeaderNotFound("В запросе не указан заголовок с токеном!");
        }

        jwtToken = header.substring(7);
        Optional<Token> token = tokenRepository.findByAccessToken(jwtToken);

        if (token.isPresent()) {
            deactivatedTokenRepository.save(new DeactivatedToken(
                    token.get().getAccessToken(), Date.from(Instant.now())));

            authentication.setAuthenticated(false);
            SecurityContextHolder.clearContext();

            AppError appError = new AppError(
                    FORBIDDEN.value(),
                    "Вы успешно вышли из профиля",
                    Instant.now().toString());

            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), appError);

        } else {
            throw new EntityNotFoundException("Данный токен не зарегистрирован в приложении.");
        }
    }
}
