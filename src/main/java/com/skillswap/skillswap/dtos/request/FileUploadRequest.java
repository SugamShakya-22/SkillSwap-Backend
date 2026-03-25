package com.skillswap.skillswap.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        @Schema(type = "string", format = "binary", description = "Profile image file to upload")
        MultipartFile file
) {}
