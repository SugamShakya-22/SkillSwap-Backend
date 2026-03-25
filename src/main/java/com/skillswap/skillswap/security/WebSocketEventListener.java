// src/main/java/com/skillswap/skillswap/security/WebSocketEventListener.java
package com.skillswap.skillswap.security;

import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.dtos.response.UserStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            String email = accessor.getUser().getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setIsOnline(true);
                userRepository.save(user);
                messagingTemplate.convertAndSend("/topic/status", new UserStatusDTO(user.getId(), true));
            });
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            String email = accessor.getUser().getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setIsOnline(false);
                user.setLastSeen(LocalDateTime.now());
                userRepository.save(user);
                messagingTemplate.convertAndSend("/topic/status", new UserStatusDTO(user.getId(), false));
            });
        }
    }
}
