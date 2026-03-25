package com.skillswap.skillswap.config;

import com.skillswap.skillswap.security.filter.JwtAuthenticationFilter;
import com.skillswap.skillswap.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // -------------------- Using JWT filter --------------------
        JwtAuthenticationFilter jwtAuthFilter =
                new JwtAuthenticationFilter(jwtService, userDetailsService);

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // -------------------- Endpoint permissions --------------------
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/request-reset",
                                "/api/auth/verify-reset",
                                "/api/websocket-test/**",
                                "/ping",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/docs/**",
                                "/ws/**",
                                "/api/skills/**",
                                "/uploads/**"  // ✅ Serve uploaded files publicly
                        ).permitAll()

                        // Admin endpoints - Requires ADMIN role
                        .requestMatchers("/api/admin/**" ).hasRole("ADMIN")

                        // ✅ IMPORTANT: /api/messages/** includes /api/messages/with-files
                        // This should work for ALL authenticated users
                        .requestMatchers(
                                "/api/skills/**",
                                "/api/user/**",
                                "/user/**",
                                "/api/match/**",
                                "/match/**",
                                "/api/messages/**",  // ✅ This includes with-files endpoint
                                "/messages/**",
                                "/api/profile/**",
                                "/api/auth/change-password",
                                "/api/auth/logout",
                                "/api/chat/**",
                                "/api/reviews/**"
                        ).authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // stateless: do not store any session or data in database
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //  Allow all origins for mobile development
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Allowed methods that we can use
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}