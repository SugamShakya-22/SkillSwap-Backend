package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.ReactionRequest;
import com.skillswap.skillswap.dtos.request.SendMessageRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.MessageResponse;
import com.skillswap.skillswap.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messaging")
public class MessageController {

    private final MessageService messageService;

    /**
     * Send a text message (JSON)
     */
    @PostMapping
    public ResponseEntity<ApiResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        log.info("📨 Received text message request from: {}", authentication.getName());
        String email = authentication.getName();
        return ResponseEntity.ok(
                new ApiResponse(
                        "Message sent",
                        true,
                        messageService.sendMessage(request, email)
                )
        );
    }

    /**
     * ✅ Send message with file attachments (multipart/form-data)
     */
    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> sendMessageWithFiles(
            @RequestParam Long receiverId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<MultipartFile> files,
            Authentication authentication
    ) {
        log.info("📎 Received file upload request from: {}", authentication.getName());
        log.info("   Receiver ID: {}", receiverId);
        log.info("   Content: {}", content);
        log.info("   Files count: {}", files != null ? files.size() : 0);

        String email = authentication.getName();

        SendMessageRequest request = new SendMessageRequest(receiverId, content, files);

        MessageResponse response = messageService.sendMessage(request, email);

        log.info("✅ File message sent successfully: {}", response.messageId());

        return ResponseEntity.ok(
                new ApiResponse(
                        "Message sent with files",
                        true,
                        response
                )
        );
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<ApiResponse> getConversation(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                new ApiResponse(
                        "Conversation fetched",
                        true,
                        messageService.getConversation(userId, email)
                )
        );
    }

    @PatchMapping("/read/{messageId}")
    public ResponseEntity<ApiResponse> markMessageAsRead(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        MessageResponse updated = messageService.markMessageAsRead(messageId, email);

        return ResponseEntity.ok(
                new ApiResponse("Message marked as read", true, updated)
        );
    }

    /**
     * ✅ Add or update reaction on a message
     */
    @PostMapping("/reaction")
    public ResponseEntity<ApiResponse> addReaction(
            @Valid @RequestBody ReactionRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        MessageResponse updated = messageService.addReaction(request, email);

        return ResponseEntity.ok(
                new ApiResponse("Reaction added", true, updated)
        );
    }

    /**
     * ✅ Remove reaction from a message
     */
    @DeleteMapping("/reaction/{messageId}")
    public ResponseEntity<ApiResponse> removeReaction(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        ReactionRequest request = new ReactionRequest(messageId, null);
        MessageResponse updated = messageService.addReaction(request, email);

        return ResponseEntity.ok(
                new ApiResponse("Reaction removed", true, updated)
        );
    }
}