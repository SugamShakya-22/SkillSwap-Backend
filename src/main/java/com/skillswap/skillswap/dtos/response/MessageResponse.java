package com.skillswap.skillswap.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponse(
        Long messageId,
        Long senderId,
        Long receiverId,
        String content,
        Boolean isRead,
        LocalDateTime createdAt,
        String reaction, // ✅ NEW: reaction emoji
        List<AttachmentResponse> attachments // ✅ NEW: file attachments
) {}