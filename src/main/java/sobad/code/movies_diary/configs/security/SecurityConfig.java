package sobad.code.movies_diary.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sobad.code.movies_diary.jwts.JwtAuthenticationFilter;


import java.util.Arrays;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static sobad.code.movies_diary.controllers.AuthController.AUTH_CONTROLLER_REFRESH_TOKEN_PATH;
import static sobad.code.movies_diary.controllers.MovieController.MOVIE_CONTROLLER_PATH_USERS;
import static sobad.code.movies_diary.controllers.UserController.USER_CONTROLLER_PATH;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher(AUTH_CONTROLLER_REFRESH_TOKEN_PATH, GET.toString()),
            new AntPathRequestMatcher(AUTH_CONTROLLER_LOGIN_PATH, POST.toString()),
            new AntPathRequestMatcher(MOVIE_CONTROLLER_PATH_USERS, GET.toString()),
            new AntPathRequestMatcher(USER_CONTROLLER_PATH, POST.toString()),
            new AntPathRequestMatcher("/h2/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/api-docs.html")
    );


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfig -> corsConfig.configurationSource(
                        request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Arrays.asList("*"));
                            configuration.setAllowedHeaders(Arrays.asList("*"));
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            return configuration;
                        }
                ))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests.requestMatchers(PUBLIC_URLS).permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                .exceptionHandling(ex -> {
//                    ex.authenticationEntryPoint(((request, response, authException) -> {
//                        response.setContentType(APPLICATION_JSON_VALUE);
//                        response.setCharacterEncoding(String.valueOf(UTF_8));
//                        response.setStatus(SC_FORBIDDEN);
//                        AppError appError = new AppError(
//                                SC_FORBIDDEN,
//                                "Авторизируйтесь для выполнения этого действия",
//                                LocalDateTime.now().toString());
//                        objectMapper.writeValue(response.getWriter(), appError);
//                    }));
//                });

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/h2/**")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("/swagger-ui/**");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/api/**")
                        .allowedMethods("*")
                        .allowedOrigins("http://localhost:8000");
            }
        };
    }
}
