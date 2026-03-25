package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotNull(message = "Reviewee ID is required")
        Long revieweeId,

        @NotNull(message = "Match request ID is required")
        Long matchRequestId,

        @NotBlank(message = "Comment is required")
        @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
        String comment
) {}