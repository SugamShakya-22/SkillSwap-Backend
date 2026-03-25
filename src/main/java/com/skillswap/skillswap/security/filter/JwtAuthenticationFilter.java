package com.skillswap.skillswap.security.filter;

import com.skillswap.skillswap.service.security.JwtService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip public endpoints
        return path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/request-reset") ||
                path.startsWith("/api/auth/verify-reset") ||
                path.startsWith("/ping") ||
//                path.startsWith("/api/skills/all") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/ws") ||
                path.startsWith("/uploads");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();

        // ✅ Enhanced logging for debugging
        log.info("🔍 JWT Filter - {} {} | Content-Type: {}", method, requestURI, contentType);
        log.info("   Authorization Header: {}", authHeader != null ? "Present" : "Missing");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("⚠️ No valid Authorization header found for {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String username = jwtService.extractUsername(token);

            log.info("📝 Token extracted for user: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // ✅ Verify it's an access token (not refresh token)
                if (jwtService.isAccessToken(token) && !jwtService.isTokenExpired(token)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("✅ Authentication successful for: {} | Endpoint: {}", username, requestURI);
                } else {
                    log.error("❌ Invalid or expired access token for: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("❌ JWT Authentication error for {} {}: {}", method, requestURI, e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}