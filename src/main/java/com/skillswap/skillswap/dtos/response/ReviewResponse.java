package com.skillswap.skillswap.dtos.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Long reviewerId,
        String reviewerUsername,
        String reviewerAvatarUrl,
        Long revieweeId,
        String revieweeUsername,
        Long matchRequestId,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}