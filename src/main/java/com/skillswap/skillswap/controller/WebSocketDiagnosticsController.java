package com.skillswap.skillswap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * DIAGNOSTIC CONTROLLER - To test WebSocket user resolution
 * DELETE AFTER TESTING!
 */
@Slf4j
@RestController
@RequestMapping("/api/websocket-test")
@RequiredArgsConstructor
public class WebSocketDiagnosticsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    /**
     * Test if Spring can resolve user sessions
     * GET /api/websocket-test/users
     */
    @GetMapping("/users")
    public Map<String, Object> getConnectedUsers() {
        log.info("========================================");
        log.info("📊 WEBSOCKET CONNECTED USERS CHECK");

        Map<String, Object> result = new HashMap<>();

        int userCount = userRegistry.getUserCount();
        log.info("   Total connected users: {}", userCount);
        result.put("totalUsers", userCount);

        result.put("users", new java.util.ArrayList<>());
        userRegistry.getUsers().forEach(user -> {
            log.info("   - User: {} (Sessions: {})", user.getName(), user.getSessions().size());
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("name", user.getName());
            userInfo.put("sessionCount", user.getSessions().size());
            ((java.util.ArrayList) result.get("users")).add(userInfo);
        });

        log.info("========================================");
        return result;
    }

    /**
     * Manually broadcast to a specific user
     * GET /api/websocket-test/broadcast?userId=2
     */
    @GetMapping("/broadcast")
    public Map<String, Object> testBroadcast(@RequestParam String userId) {
        log.info("========================================");
        log.info("🧪 TESTING BROADCAST TO USER: {}", userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("timestamp", LocalDateTime.now());

        try {
            // Create test message
            Map<String, Object> testMessage = new HashMap<>();
            testMessage.put("messageId", 9999L);
            testMessage.put("senderId", 1L);
            testMessage.put("receiverId", Long.parseLong(userId));
            testMessage.put("content", "🧪 TEST MESSAGE - If you see this, broadcasting works!");
            testMessage.put("isRead", false);
            testMessage.put("createdAt", LocalDateTime.now());
            testMessage.put("attachments", new java.util.ArrayList<>());

            // Test Method 1: convertAndSendToUser
            log.info("   📤 Method 1: convertAndSendToUser");
            log.info("      User: {}", userId);
            log.info("      Destination: /queue/messages");
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/messages",
                    testMessage
            );
            log.info("   ✅ Method 1 executed");

            // Test Method 2: convertAndSend with full path
            log.info("   📤 Method 2: convertAndSend");
            String fullDest = "/user/" + userId + "/queue/messages";
            log.info("      Full destination: {}", fullDest);
            messagingTemplate.convertAndSend(fullDest, testMessage);
            log.info("   ✅ Method 2 executed");

            result.put("success", true);
            result.put("message", "Both broadcast methods executed. Check frontend console!");

        } catch (Exception e) {
            log.error("❌ Broadcast failed", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        log.info("========================================");
        return result;
    }

    /**
     * Check if messagingTemplate is available
     * GET /api/websocket-test/status
     */
    @GetMapping("/status")
    public Map<String, Object> checkStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("messagingTemplateAvailable", messagingTemplate != null);
        status.put("userRegistryAvailable", userRegistry != null);
        status.put("timestamp", LocalDateTime.now());

        log.info("WebSocket Status:");
        log.info("   messagingTemplate: {}", messagingTemplate != null ? "Available" : "NULL");
        log.info("   userRegistry: {}", userRegistry != null ? "Available" : "NULL");

        return status;
    }
}