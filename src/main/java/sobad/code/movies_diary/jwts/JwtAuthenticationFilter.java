package sobad.code.movies_diary.jwts;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.repositories.DeactivatedTokenRepository;
import sobad.code.movies_diary.service.UserService;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtTokenUtils.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (jwtTokenUtils.isTokenValid(jwt, userDetails)) {
                if (deactivatedTokenRepository.findByToken(jwt).isEmpty()) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new RuntimeException("ТОКЕН ПИДАРАСА");
                }
            }
//            else {
//                AppError appError = new AppError(
//                        FORBIDDEN.value(),
//                        "Токен доступа истек, обновите токен доступа",
//                        LocalDateTime.now().toString());
//
//                response.setStatus(FORBIDDEN.value());
//                response.setContentType(APPLICATION_JSON_VALUE);
//                response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.writeValue(response.getWriter(), appError);
//            }
        }
        filterChain.doFilter(request, response);
    }
}
