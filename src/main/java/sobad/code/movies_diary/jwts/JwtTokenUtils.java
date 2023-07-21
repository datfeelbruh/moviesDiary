package sobad.code.movies_diary.jwts;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.service.UserService;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final UserService userService;
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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, jwtRefreshTokenExpiration);
    }

    public String buildToken(Map<String, Object> claims, User user, Long jwtAccessTokenExpiration) {
        Instant now = Instant.now();
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        claims.put("role", userDetails.getAuthorities().toString());
        claims.put("id", user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
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
