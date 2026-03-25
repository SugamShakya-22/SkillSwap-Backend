package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
        @NotBlank(message = "Comment is required")
        @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
        String comment
) {}