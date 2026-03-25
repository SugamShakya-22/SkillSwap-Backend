package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotNull;

public record ReactionRequest(
        @NotNull(message = "Message ID is required")
        Long messageId,

        String reaction // Can be null to remove reaction
) {}