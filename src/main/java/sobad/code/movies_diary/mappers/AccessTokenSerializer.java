package sobad.code.movies_diary.mappers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.jwts.JwtToken;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
@RequiredArgsConstructor
public class AccessTokenSerializer implements Function<JwtToken, String> {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public String apply(JwtToken token) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", token.getAuthorities());
        claims.put("userId", token.getUserId());
        claims.put("tokenId", token.getId());


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(token.getSubject())
                .setIssuedAt(Date.from(token.getCreatedAt()))
                .setExpiration(Date.from(token.getExpiredAt()))
                .signWith(getSignKey(), HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
