package com.skillswap.skillswap.dtos.response;

public record ProfileResponse(
        String fullName,
        String bio,
        String avatarUrl,
        String username,
        String email
) {
}
