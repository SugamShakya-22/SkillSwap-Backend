package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.ChatMessageRequest;
import com.skillswap.skillswap.dtos.request.SendMessageRequest;
import com.skillswap.skillswap.dtos.response.ChatReadReceipt;
import com.skillswap.skillswap.dtos.response.MessageResponse;
import com.skillswap.skillswap.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle incoming chat messages via WebSocket
     * Client sends to: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        log.info("========================================");
        log.info("💬 WEBSOCKET MESSAGE RECEIVED");
        log.info("   From: {}", principal != null ? principal.getName() : "NULL");
        log.info("   To User ID: {}", request.receiverId());
        log.info("   Content: {}", request.content());
        log.info("========================================");

        try {
            String senderEmail = principal.getName();

            // Save message to database
            MessageResponse saved = messageService.sendMessage(
                    new SendMessageRequest(
                            request.receiverId(),
                            request.content(),
                            null  // No files in WebSocket messages
                    ),
                    senderEmail
            );

            log.info("✅ Message saved with ID: {}", saved.messageId());
            log.info("   Sender ID: {}", saved.senderId());
            log.info("   Receiver ID: {}", saved.receiverId());

            // ✅ NOTE: MessageServiceImpl already broadcasts to both users
            // So we don't need to broadcast again here
            log.info("✅ WebSocket message handling complete (MessageService handles broadcast)");
            log.info("========================================\n");

        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR IN WEBSOCKET MESSAGE HANDLER");
            log.error("   Error type: {}", e.getClass().getName());
            log.error("   Error message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("========================================\n");
        }
    }

    /**
     * Handle read receipts via WebSocket
     * Client sends to: /app/chat.read
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ChatReadReceipt receipt, Principal principal) {
        log.info("========================================");
        log.info("📖 READ RECEIPT REQUEST");
        log.info("   From: {}", principal != null ? principal.getName() : "NULL");
        log.info("   Message ID: {}", receipt.messageId());
        log.info("========================================");

        try {
            String email = principal.getName();

            MessageResponse updated = messageService.markMessageAsRead(
                    receipt.messageId(),
                    email
            );

            log.info("✅ Message marked as read");
            log.info("   Sender will be notified via WebSocket");
            log.info("========================================\n");

            // ✅ NOTE: MessageServiceImpl already broadcasts read receipt
            // So we don't need to broadcast again here

        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR IN READ RECEIPT HANDLER");
            log.error("   Error message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("========================================\n");
        }
    }
}