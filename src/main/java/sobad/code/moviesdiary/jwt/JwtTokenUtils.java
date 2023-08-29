package sobad.code.moviesdiary.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.authentication_exceptions.CustomJwtExpiredException;
import sobad.code.moviesdiary.mappers.token_serializer.AccessTokenSerializer;
import sobad.code.moviesdiary.services.UserService;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtAccessTokenExpiration;

    public JwtToken createToken(User user, UserDetails userDetails) {
        return JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(userDetails.getUsername())
                .userId(user.getId())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, DAYS))
                .build();
    }

    public JwtToken refreshToken(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        String subject = extractUsername(jwtToken);
        List<String> roles = extractRoles(jwtToken);
        Long userId = claims.get("userId", Long.class);

        return JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(subject)
                .userId(userId)
                .authorities(roles)
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(jwtAccessTokenExpiration, DAYS))
                .build();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return (ArrayList<String>) claims.get("role");
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new CustomJwtExpiredException("Токен доступа истек, обновите токен доступа.");
        } catch (MalformedJwtException e) {
            log.error(e.getMessage());
            throw new MalformedJwtException("Токен доступа некорректный.");
        }

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
