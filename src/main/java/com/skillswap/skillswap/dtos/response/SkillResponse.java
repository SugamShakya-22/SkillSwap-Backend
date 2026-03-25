package com.skillswap.skillswap.dtos.response;

public record SkillResponse(
        Long skillId,
        String skillName,
        String category,
        String description
) {}
