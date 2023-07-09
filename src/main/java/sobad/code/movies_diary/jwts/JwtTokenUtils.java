package sobad.code.movies_diary.jwts;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sobad.code.movies_diary.entities.User;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Service
public class JwtTokenUtils {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtAccessTokenExpiration;
    @Value(("${jwt.refresh-token.expiration}"))
    private long jwtRefreshTokenExpiration;

    public String generateAccessToken(User user) {
        return buildToken(new HashMap<>(), user, jwtAccessTokenExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    public String generateAccessToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(extraClaims, user, jwtAccessTokenExpiration);
    }

    public String generateRefreshToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(extraClaims, user, jwtRefreshTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, jwtRefreshTokenExpiration);
    }

    public String buildToken(Map<String, Object> claims, User user, Long jwtAccessTokenExpiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(jwtAccessTokenExpiration, SECONDS)))
                .signWith(HS256, secretKey)
                .compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(Instant.now());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
