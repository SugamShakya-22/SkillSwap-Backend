package com.skillswap.skillswap.dtos.response;

public record AttachmentResponse(
        Long id,
        String fileName,
        String fileUrl,
        String mimeType,
        Long fileSize
) {}