package sobad.code.moviesdiary.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sobad.code.moviesdiary.dtos.ResponseMessage;

import java.io.IOException;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sobad.code.moviesdiary.configs.security.SecurityConfig.PUBLIC_URLS;

@Component
public class ExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        boolean isPrivateUrl = !PUBLIC_URLS.matches(request);

        if (authHeader == null && isPrivateUrl) {
            ResponseMessage appError = new ResponseMessage(
                    FORBIDDEN.value(),
                    "Авторизируйтесь для выполнения этого запроса.",
                    Instant.now().toString());

            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), appError);
        }
        filterChain.doFilter(request, response);
    }
}
