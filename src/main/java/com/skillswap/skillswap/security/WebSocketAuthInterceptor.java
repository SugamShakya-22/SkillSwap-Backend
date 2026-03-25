package com.skillswap.skillswap.security;

import com.skillswap.skillswap.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Get Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            log.info("WebSocket CONNECT - Authorization header: {}", authHeader != null ? "Present" : "Missing");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // ✅ Extract email using your existing method
                    String email = jwtService.extractUsername(token);
                    log.info("WebSocket CONNECT - Extracted email: {}", email);

                    // ✅ Validate token: Check if it's an access token (your existing method)
                    if (email != null && jwtService.isAccessToken(token)) {
                        // Load user details
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        // Create authentication
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // ✅ CRITICAL: Set user in StompHeaderAccessor
                        // This makes Principal available in @MessageMapping methods
                        accessor.setUser(authentication);

                        log.info("✅ WebSocket authentication successful for: {}", email);
                    } else {
                        if (email == null) {
                            log.warn("❌ Could not extract email from token");
                        } else {
                            log.warn("❌ Token is not an access token (might be refresh token)");
                        }
                    }
                } catch (Exception e) {
                    log.error("❌ Error authenticating WebSocket connection: {}", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                log.warn("❌ No Authorization header in WebSocket CONNECT");
            }
        }

        return message;
    }
}