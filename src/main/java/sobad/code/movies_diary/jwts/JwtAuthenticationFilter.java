package sobad.code.movies_diary.jwts;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.repositories.TokenRepository;
import sobad.code.movies_diary.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtTokenUtils.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.findByUsername(username).orElseThrow();
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
            Boolean isTokenValid = tokenRepository.findByToken(jwt)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);
            if (jwtTokenUtils.isTokenValid(jwt, user) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
