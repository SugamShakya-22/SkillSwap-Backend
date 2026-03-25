package com.skillswap.skillswap.dtos.response;

import java.util.List;

public record UserListingResponse(
        Long userId,
        String fullName,
        String email,
        String avatarUrl,
        List<UserSkillResponse> skills
) {}
