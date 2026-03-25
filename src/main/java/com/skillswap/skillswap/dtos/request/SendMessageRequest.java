package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SendMessageRequest(
        @NotNull(message = "Receiver ID is required")
        Long receiverId,

        String content, // Optional - can be empty if only sending files

        List<MultipartFile> files // ✅ NEW: optional file attachments
) {}