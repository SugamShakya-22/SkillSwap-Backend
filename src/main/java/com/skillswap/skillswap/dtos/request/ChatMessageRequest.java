package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull Long receiverId,
        @NotBlank String content
) {}
