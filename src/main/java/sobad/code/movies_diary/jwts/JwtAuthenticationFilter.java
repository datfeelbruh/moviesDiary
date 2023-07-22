package sobad.code.movies_diary.jwts;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.exceptions.authenticationExceptions.DeactivatedTokenException;
import sobad.code.movies_diary.repositories.DeactivatedTokenRepository;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.service.UserService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sobad.code.movies_diary.configs.security.SecurityConfig.PUBLIC_URLS;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final DeactivatedTokenRepository deactivatedTokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (PUBLIC_URLS.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtTokenUtils.extractUsername(jwt);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (isValidToken(jwt, userDetails) && isActiveToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
               AppError appError = new AppError(
                       FORBIDDEN.value(),
                       "Токен доступа деактивирован, обновите токен.",
                       Instant.now().toString());

               response.setStatus(FORBIDDEN.value());
               response.setContentType(APPLICATION_JSON_VALUE);
               response.setCharacterEncoding(UTF_8.toString());
               ObjectMapper objectMapper = new ObjectMapper();

               objectMapper.writeValue(response.getWriter(), appError);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token, UserDetails userDetails) {
        return jwtTokenUtils.isTokenValid(token, userDetails);
    }

    private boolean isActiveToken(String token) {
        return !deactivatedTokenRepository.existsByToken(token);
    }
}
