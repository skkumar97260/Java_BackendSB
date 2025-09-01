package com.pixalive.backend.config;

import com.pixalive.backend.middleware.BasicAuthFilter;
import com.pixalive.backend.middleware.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BasicAuthFilter basicAuthFilter;

    public SecurityConfig(
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                          BasicAuthFilter basicAuthFilter) {
      this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.basicAuthFilter = basicAuthFilter;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilter(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow signup and login without JWT
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                       .requestMatchers(HttpMethod.GET, "/api/users/all").permitAll()
                        // ✅ Everything else requires authentication jwt
                        .anyRequest().authenticated()
                )
                // 👇 Custom JSON error when JWT is missing/invalid
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))

                // Filters order: BasicAuth → JWT
             .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
              .addFilterAfter(jwtAuthenticationFilter, BasicAuthFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 👇 Custom entry point for JSON error
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\": \"Missing or invalid JWT Auth\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
    // ✅ Enable CORS for Angular (http://localhost:4200)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://pixalivetech.com",
                "https://www.pixalivetech.com"
        ));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
