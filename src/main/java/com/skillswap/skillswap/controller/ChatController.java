package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getChatUsers(Authentication authentication) {

        return ResponseEntity.ok(
                new ApiResponse(
                        "Chat users fetched",
                        true,
                        chatService.getChatUsers(authentication.getName())
                )
        );
    }
}
