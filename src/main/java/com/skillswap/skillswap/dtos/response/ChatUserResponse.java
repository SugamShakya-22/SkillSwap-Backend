package com.skillswap.skillswap.dtos.response;

import java.time.LocalDateTime;

public record ChatUserResponse(
        Long userId,
        String username,
        String avatarUrl,
        String lastMessage,
        LocalDateTime lastMessageTime,
        boolean isOnline,
        int unreadCount
) {
}